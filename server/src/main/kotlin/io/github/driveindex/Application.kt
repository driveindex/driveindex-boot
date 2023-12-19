package io.github.driveindex

import io.github.driveindex.configuration.FeignClientConfig
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.log
import jakarta.annotation.PostConstruct
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ApplicationContext
import org.springframework.http.converter.AbstractKotlinSerializationHttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import kotlin.reflect.KClass

@EnableFeignClients(defaultConfiguration = [FeignClientConfig::class])
@EnableScheduling
@SpringBootApplication
class Application {
    @Autowired
    private lateinit var converters: HttpMessageConverters
    @PostConstruct
    fun init() {
        for (converter in converters.converters) {
            if (converter !is KotlinSerializationJsonHttpMessageConverter) {
                continue
            }
            val json = AbstractKotlinSerializationHttpMessageConverter::class.java
                    .getDeclaredField("format")
                    .also {
                        it.isAccessible = true
                    }
                    .get(converter) as Json
            log.debug("json.configuration: ${json.configuration}")
        }
    }

    companion object {
        const val APPLICATION_BASE_NAME = "DriveIndex"
        val APPLICATION_BASE_NAME_LOWER = APPLICATION_BASE_NAME.lowercase(Locale.getDefault())

        private lateinit var context: ApplicationContext
        val Context: ApplicationContext get() = context

        @JvmStatic
        fun main(args: Array<String>) {
            setupConfig(args)
            context = Bootstrap(Application::class.java)
                .setPort(ConfigManager.Port)
                .setDatasource(
                        ConfigManager.SqlDatabaseHost,
                        ConfigManager.SqlDatabaseName,
                        ConfigManager.SqlUsername,
                        ConfigManager.SqlPassword,
                )
                .setDebug(ConfigManager.Debug)
                .setLogPath(ConfigManager.LogPath)
                .run(args)
        }

        private fun setupConfig(args: Array<String>) {
            var configPath: String? = null
            for (arg in args) {
                if (arg.startsWith("--config=")) {
                    configPath = arg.substring(9)
                    return
                }
            }
            System.getenv("DRIVEINDEX_CONFIG")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { configPath = it }
            ConfigManager.setConfigFile(configPath)
        }

        inline fun <reified T> getBean(): T {
            return Context.getBean(T::class.java)
        }

        val <T: Any> KClass<T>.Bean: T get() {
            return Context.getBean(java)
        }
    }
}


private class Bootstrap(clazz: Class<*>) {
    private val application: SpringApplication = SpringApplication(clazz)
    private val properties: MutableMap<String, Any> = HashMap()

    fun setDatasource(
            dbHost: String, dbDatabase: String,
            dbUsername: String, dbPassword: String
    ): Bootstrap {
        properties["spring.datasource.username"] = dbUsername
        properties["spring.datasource.password"] = dbPassword
        properties["spring.datasource.url"] = "jdbc:mariadb://$dbHost/$dbDatabase"
        return this
    }

    fun setPort(port: Int): Bootstrap {
        properties["server.port"] = port
        return this
    }

    fun setDebug(isDebug: Boolean): Bootstrap {
        if (isDebug) {
            properties["spring.profiles.active"] = "dev"
        } else {
            properties["spring.profiles.active"] = "prod"
        }
        return this
    }

    fun test(): Bootstrap {
        properties["spring.profiles.active"] = "test"
        return this
    }

    fun setLogPath(path: String): Bootstrap {
        properties["driveindex.logging.path"] = path
        return this
    }

    fun run(args: Array<String>): ApplicationContext {
        application.setDefaultProperties(properties)
        return application.run(*args)
    }
}
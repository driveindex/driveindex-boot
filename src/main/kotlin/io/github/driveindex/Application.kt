package io.github.driveindex

import io.github.driveindex.core.ConfigManager
import jakarta.annotation.PostConstruct
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*

@EnableScheduling
@SpringBootApplication
class Application {
    @PostConstruct
    fun setup() {


    }

    companion object {
        const val APPLICATION_BASE_NAME = "DriveIndex"
        val APPLICATION_BASE_NAME_LOWER = APPLICATION_BASE_NAME.lowercase(Locale.getDefault())

        @JvmStatic
        fun main(args: Array<String>) {
            Bootstrap(Application::class.java)
                .setPort(ConfigManager.Port)
                .setDatasource(
                        ConfigManager.SqlDatabasePath,
                        ConfigManager.SqlUsername,
                        ConfigManager.SqlPassword
                )
                .setDebug(ConfigManager.Debug)
                .setLogPath(ConfigManager.LogPath)
                .run(args)
        }
    }
}


private class Bootstrap(clazz: Class<*>) {
    private val application: SpringApplication = SpringApplication(clazz)
    private val properties: MutableMap<String, Any> = HashMap()

    fun setPort(port: Int): Bootstrap {
        properties["server.port"] = port
        return this
    }

    fun setDatasource(
            dbPath: String, dbUsername: String, dbPassword: String
    ): Bootstrap {
        properties["spring.datasource.username"] = dbUsername
        properties["spring.datasource.password"] = dbPassword
        properties["spring.datasource.url"] = "jdbc:h2:file:$dbPath/${Application.APPLICATION_BASE_NAME_LOWER}"
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

    fun setLogPath(path: String): Bootstrap {
        properties["driveindex.logging.path"] = path
        return this
    }

    fun run(args: Array<String>) {
        application.setDefaultProperties(properties)
        application.run(*args)
    }
}
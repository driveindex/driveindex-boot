package io.github.driveindex

import io.github.driveindex.core.ConfigManager
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application {
    companion object {
        const val APPLICATION_BASE_NAME = "DriveIndex"
        const val APPLICATION_BASE_NAME_LOWER = "driveidnex"

        @JvmStatic
        fun main(args: Array<String>) {
            Bootstrap(Application::class.java)
                .setPort(ConfigManager.Port)
                .setDatasource(
                    ConfigManager.SqlDatabaseName,
                    ConfigManager.SqlDatabaseHost,
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
        dbName: String, dbHost: String,
        dbUsername: String, dbPassword: String
    ): Bootstrap {
        properties["spring.datasource.username"] = dbUsername
        properties["spring.datasource.password"] = dbPassword
        properties["spring.datasource.url"] = "jdbc:mariadb://$dbHost/$dbName"
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
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import io.github.driveindex.findStringProperty
import io.github.driveindex.parentFile
import io.github.driveindex.requireStringProperty


plugins {
    id("com.bmuschko.docker-spring-boot-application")
}

findStringProperty("publishing.gitlab.registry.host")?.let { registryHost ->
    docker {
        // https://docs.gradle.org/current/userguide/kotlin_dsl.html#groovy_closures_from_kotlin
        registryCredentials {
            url = "https://registry.sgpublic.xyz/v2/"
            username = "mhmzx"
            password = requireStringProperty("publishing.gitlab.token")
            email = "sgpublic2002@gmail.com"
        }
    }

    val output = File(buildDir, "libs/${project.name}-$version.jar")
    tasks.register<Copy>("syncWebAppArchive") {
        dependsOn("assemble", ":web:npmRunBuild")
        from(output, File(findProject(":web")!!.buildDir, "dist"))
        into(tasks.getByName<Dockerfile>("createDockerfile").destFile.parentFile)
    }

    tasks.register<Dockerfile>("createDockerfile") {
        dependsOn("syncWebAppArchive")
        destFile = File(buildDir, "docker/Dockerfile")
        from("nginx:alpine")
        runCommand("apk add openjdk17")
        copyFile(output.name, "/app/driveindex.jar")
        copyFile("dist", "/var/www/driveindex")
        entryPoint("java")
        defaultCommand("-jar", "/app/driveindex.jar", "--config=/app/driveindex.ini")
        exposePort(8080)
        instruction("HEALTHCHECK CMD curl -f http://localhost:8080/api/health || exit 1")
    }

    tasks.register<DockerBuildImage>("buildImage") {
        dependsOn("createDockerfile")
        inputDir = tasks.getByName<Dockerfile>("createDockerfile").destFile.parentFile
        images = setOf(
            "$registryHost/drive-index/driveindex-server-boot:latest",
            "$registryHost/drive-index/driveindex-server-boot:$version",
        )
    }
}
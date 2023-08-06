
import com.github.gradle.node.npm.task.NpmTask
import io.github.driveindex.tasks.CheckoutTask

plugins {
    id("com.github.node-gradle.node")
}

private val gitRoot = File(project.buildDir, "git")

tasks.register<CheckoutTask>("cloneWebRepository") {
    url = "https://github.com/driveindex/driveindex-web-vue.git"
    root = gitRoot
}

node {
    download = true
    version = "18.9.1"
    nodeProjectDir = gitRoot
}

tasks.register<NpmTask>("npmRunBuild") {
    dependsOn("cloneWebRepository", "npmInstall")
    args = listOf("run", "build")
}
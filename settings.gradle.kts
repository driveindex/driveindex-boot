pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


rootProject.name = "driveindex-boot"

include("server")
include("web")
include("docker")

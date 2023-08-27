plugins {
    id("org.springframework.boot") version "3.0.9" apply false
    id("io.spring.dependency-management") version "1.1.2" apply false

    val kotlin = "1.9.10"
    kotlin("jvm") version kotlin apply false
    kotlin("plugin.serialization") version kotlin apply false
    kotlin("plugin.spring") version kotlin apply false
    kotlin("plugin.jpa") version kotlin apply false
}
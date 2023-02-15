import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
    kotlin("plugin.jpa") version "1.8.0"
}

group = "io.github.driveindex"
version = "2.0.0-alpha01"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("com.fasterxml.jackson.core:jackson-databind")
    }
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("com.dtflys.forest:forest-spring-boot-starter:1.5.28")

    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.0.0")
    val springDoc = "1.6.11"
    implementation("org.springdoc:springdoc-openapi-security:$springDoc")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("com.h2database:h2")

    implementation("org.ini4j:ini4j:0.5.4")
    implementation("com.google.code.gson:gson")


    val jjwt = "0.11.5"
    implementation("io.jsonwebtoken:jjwt-api:$jjwt")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwt")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:$jjwt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

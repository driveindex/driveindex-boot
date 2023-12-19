
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

group = "io.github.driveindex"
version = "2.0.0-alpha01"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.bootJar {
    archiveBaseName = "driveindex"
}

springBoot {
    mainClass = "io.github.driveindex.Application"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-json")
    }
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-websocket")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")

    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.2.0")
    val springDoc = "1.7.0"
    implementation("org.springdoc:springdoc-openapi-security:$springDoc")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    val flyway = "10.3.0"
    runtimeOnly("org.flywaydb:flyway-core:$flyway")
    runtimeOnly("org.flywaydb:flyway-mysql:$flyway")
    runtimeOnly("org.flywaydb:flyway-sqlserver:$flyway")

    implementation("org.ini4j:ini4j:0.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    val jjwt = "0.12.3"
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
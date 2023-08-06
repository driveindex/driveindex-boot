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
java.sourceCompatibility = JavaVersion.VERSION_17

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
//    implementation("org.springframework.boot:spring-boot-starter-websocket")


//    implementation("com.dtflys.forest:forest-spring-boot-starter:1.5.28")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.1")

    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.0.0")
    val springDoc = "1.6.11"
    implementation("org.springdoc:springdoc-openapi-security:$springDoc")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

    val flyway = "9.19.1"
    runtimeOnly("org.flywaydb:flyway-core:$flyway")
    runtimeOnly("org.flywaydb:flyway-mysql:$flyway")
    runtimeOnly("org.flywaydb:flyway-sqlserver:$flyway")

    implementation("org.ini4j:ini4j:0.5.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

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

fun findStringProperty(name: String): String? {
    return System.getenv(name.replace(".", "_"))
        ?.takeIf { it.isNotBlank() }
        ?: findProperty(name)?.toString()
}
fun requireStringProperty(name: String): String {
    return findStringProperty(name)
        ?: throw IllegalArgumentException("property not found")
}

findStringProperty("publishing.gitlab.registry.host").let { registryHost ->
    tasks.bootBuildImage {
        imageName.set("$registryHost/drive-index/driveindex-server-boot")
        publish.set(true)
        tags.set(listOf("latest", version.toString()))
        environment.set(mapOf(
            "BP_SPRING_CLOUD_BINDINGS_DISABLED" to true.toString(),
            "BPL_SPRING_CLOUD_BINDINGS_DISABLED" to true.toString(),
        ))
        docker {
            host.set(findStringProperty("publishing.docker.sock")
                ?: "unix:///var/run/docker.sock")
            publishRegistry {
                username.set("mhmzx")
                token.set(requireStringProperty("publishing.gitlab.token"))
            }
        }
    }
}
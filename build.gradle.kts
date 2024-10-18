plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val springCloudStarter = "2.0.2"
val springCloudStarterOpenfeign = "4.0.2"
val springRetry = "2.0.2"
val jedis = "4.3.2"
val jacksonDatabind = "2.14.2"
val slf4j = "2.0.5"
val logback = "1.4.6"
val lombok = "1.18.26"
val mapstruct = "1.5.3.Final"
val testcontainersBom = "1.17.6"
val testcontainersRedis = "1.4.6"
val junitJupiterParams = "5.9.2"
val assertj = "3.24.2"
val openapi = "2.5.0"

dependencies {
    /**
     * Spring boot starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springCloudStarter}")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:${springCloudStarterOpenfeign}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.retry:spring-retry:${springRetry}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${openapi}")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:${jedis}")
    runtimeOnly("org.postgresql:postgresql")
    //runtimeOnly ("com.h2database:h2")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonDatabind}")
    implementation("org.slf4j:slf4j-api:${slf4j}")
    implementation("ch.qos.logback:logback-classic:${logback}")
    implementation("org.projectlombok:lombok:${lombok}")
    annotationProcessor("org.projectlombok:lombok:${lombok}")
    implementation("org.mapstruct:mapstruct:${mapstruct}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapstruct}")

    /**
     * Test containers
     */
    implementation(platform("org.testcontainers:testcontainers-bom:${testcontainersBom}"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:${testcontainersRedis}")

    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitJupiterParams}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
    archiveFileName.set("service.jar")
}

jacoco {
    toolVersion = "0.8.9"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            isEnabled = true
            element = "CLASS"
            includes = listOf(
                "school.faang.user_service.service.*",
                "school.faang.user_service.cash.*",
                "school.faang.user_service.controller.*",
                "school.faang.user_service.encoder.*",
                "school.faang.user_service.generator.*",
                "school.faang.user_service.validator.*"
            )
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

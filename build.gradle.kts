import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springVersion = "3.0.0"
val jacksonVersion = "2.14.1"
val prometheusVersion = "1.10.2"
val logbackEncoderVersion = "7.2"
val navTokenSupportVersion = "3.0.0"
val hibernateValidatorVersion = "7.0.4.Final"
val azureAdClientVersion = "0.0.7"
val mockWebserverVersion = "4.9.3"

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    id("com.github.ben-manes.versions") version "0.52.0"
}

group = "no.nav.pensjon.opptjening"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.github.com/navikt/pensjon-opptjening-azure-ad-client") {
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Log and metric
    implementation("io.micrometer:micrometer-registry-prometheus:$prometheusVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    // OIDC
    implementation("no.nav.security:token-validation-spring:$navTokenSupportVersion")
    implementation("no.nav.security:token-client-spring:$navTokenSupportVersion")
    implementation("org.hibernate:hibernate-validator:$hibernateValidatorVersion")
    implementation("no.nav.pensjonopptjening:pensjon-opptjening-azure-ad-client:$azureAdClientVersion")

    //Documentation
    //implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // Test - setup
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner:3.0.0")

    // Test - token-validation-spring-test dependencies
    testImplementation("no.nav.security:token-validation-spring-test:$navTokenSupportVersion")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(PASSED, FAILED, SKIPPED)
    }
}

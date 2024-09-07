import nu.studer.gradle.jooq.JooqEdition
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.24"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("nu.studer.jooq") version "9.0"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
val kotestVersion = "5.9.1"
val kotestSpringExtensionVersion = "1.3.0"
val mockkVersion = "1.13.12"
val jjwtVersion = "0.12.6"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    runtimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestSpringExtensionVersion")

    // mockk
    testImplementation("io.mockk:mockk:$mockkVersion")

    // passwordEncoder
    implementation("org.springframework.security:spring-security-crypto")

    // jjwt
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // JooQ
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    jooqGenerator("com.h2database:h2")

    // jackson (isXXX 필드 is 제거 문제 해결 용도)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    dependsOn("asciidoctor")
    from("/build/docs/asciidoc") {
        into("BOOT-INF/classes/static/docs")
    }
}

tasks.build {
    dependsOn("copyDocs")
}
tasks.processResources {
    dependsOn("copyDocs")
}

tasks.register<Copy>("copyDocs") {
    description = "Restdocs 문서를 resources/static/docs 디렉토리로 복사합니다."
    group = "documentation"

    dependsOn("asciidoctor")

    destinationDir = file("src/main/resources/static/docs")
    delete("src/main/resources/static/docs")
    from("build/docs/asciidoc") {
        this.into("")
    }
}


jooq {
    version.set("3.19.10")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                jdbc.apply {
                    driver = "org.h2.Driver"
                    url = "jdbc:h2:~/codereview;AUTO_SERVER=TRUE"
                    user = "sa"
                    password = ""
                    properties = listOf(
                        Property().apply {
                            key = "PAGE_SIZE"
                            value = "2048"
                        }
                    )
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.h2.H2Database"
                        includes = ".*"
                        excludes = ""
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = false
                        isImmutablePojos = false
                        isFluentSetters = false
                    }
                    target.apply {
                        packageName = "org.example.codereviewstudy"
                        directory = "src/generated/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

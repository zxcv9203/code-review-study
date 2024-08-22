plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.24"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
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
    testImplementation ("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation ("io.kotest:kotest-assertions-core:$kotestVersion")
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
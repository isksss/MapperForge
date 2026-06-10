plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.diffplug.spotless") version "8.6.0"
}

group = "io.github.isksss"
version = "0.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val integrationTestSourceSet =
    sourceSets.create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += output + compileClasspath + configurations.runtimeClasspath.get()
    }

dependencies {
    implementation("com.fasterxml.woodstox:woodstox-core:7.1.1")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.snakeyaml:snakeyaml-engine:3.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    "integrationTestImplementation"(sourceSets.main.get().output)
    "integrationTestImplementation"("org.junit.jupiter:junit-jupiter:5.14.1")
    "integrationTestImplementation"("org.mybatis:mybatis:3.5.19")
    "integrationTestImplementation"("org.testcontainers:junit-jupiter:1.21.4")
    "integrationTestImplementation"("org.testcontainers:postgresql:1.21.4")
    "integrationTestImplementation"("org.testcontainers:mysql:1.21.4")
    "integrationTestImplementation"("org.postgresql:postgresql:42.7.11")
    "integrationTestImplementation"("com.mysql:mysql-connector-j:9.7.0")
    "integrationTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    plugins {
        create("mapperForge") {
            id = "io.github.isksss.mapperforge"
            implementationClass = "io.github.isksss.mapperforge.gradle.MapperForgePlugin"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

val integrationTest by tasks.registering(Test::class) {
    description = "Runs MyBatis integration tests against Dockerized PostgreSQL and MySQL."
    group = "verification"
    testClassesDirs = integrationTestSourceSet.output.classesDirs
    classpath = integrationTestSourceSet.runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform()
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat("1.35.0")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("*.md", "docs/**/*.md", ".mise.toml")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.gradle.plugin-publish") version "0.15.0"
}

repositories {
    gradlePluginPortal()
}

group = "com.nisecoder.gradle"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")

    implementation("it.skrape:skrapeit:1.1.5")

    // https://github.com/ktorio/ktor
    implementation(platform("io.ktor:ktor-bom:1.6.1"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-client-logging")
    implementation("ch.qos.logback:logback-classic")

    implementation("nu.studer:gradle-credentials-plugin:2.1")

    testImplementation(gradleTestKit())
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

publishing {
    repositories {
        maven {
            name = "gitHubPackages"
            url = uri("https://maven.pkg.github.com/nise-nabe/atcoder-gradle-plugin")
            credentials {
                username = project.findProperty("gpr.user")?.toString() ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key")?.toString() ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

pluginBundle {
    website = "https://nise-nabe.github.io/atcoder-gradle-plugin/"
    vcsUrl = "https://github.com/nise-nabe/atcoder-gradle-plugin"
    tags = listOf("atcoder")

    description = "AtCoder tool for Gradle"

    (plugins) {
        "com.nisecoder.gradle.atcoder" {
            displayName = "AtCoder Gradle plugin"
        }
        "com.nisecoder.gradle.atcoder.contest" {
            displayName = "Base Convention plugin for AtCoder Gradle plugin"
        }
        "com.nisecoder.gradle.atcoder.auto-include" {
            displayName = "Settings Convention plugins for AtCoder Gradle plugin"
        }
    }
}

tasks.asciidoctor {
    baseDirFollowsSourceFile()
}

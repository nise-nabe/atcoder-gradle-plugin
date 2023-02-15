rootProject.name = "atcoder-gradle-plugin"

pluginManagement {
    plugins {
        id("org.asciidoctor.jvm.convert") version "3.3.2"
        id("com.gradle.plugin-publish") version "1.1.0"
        id("org.jlleitschuh.gradle.ktlint") version "11.2.0"
    }

    repositories {
        gradlePluginPortal()
    }
}

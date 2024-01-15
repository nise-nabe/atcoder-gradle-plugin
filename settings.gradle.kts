rootProject.name = "atcoder-gradle-plugin"

pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version "1.2.1"
        id("org.jlleitschuh.gradle.ktlint") version "12.0.3"
    }

    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "atcoder-gradle-plugin"

pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version "2.1.0"
        id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    }

    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

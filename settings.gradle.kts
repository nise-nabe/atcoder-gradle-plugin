rootProject.name = "atcoder-gradle-plugin"

pluginManagement {
    plugins {
        id("com.gradle.plugin-publish") version "1.3.1"
        id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
    }

    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

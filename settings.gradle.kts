rootProject.name = "atcoder-gradle-plugin"

pluginManagement {
    plugins {
        id("org.asciidoctor.jvm.convert") version "3.3.2"
        id("com.gradle.plugin-publish") version "1.2.1"
        id("org.jlleitschuh.gradle.ktlint") version "12.0.2"
    }

    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

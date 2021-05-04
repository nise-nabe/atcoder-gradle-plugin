package com.nisecoder.gradle.atcoder

plugins {
    java
    id("com.nisecoder.gradle.atcoder.contest")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

val atcoder = extensions.getByType<AtCoderExtension>()

atcoder.contestTask.all {
    sourceSets.create(name) {
        val mainSourceSet = sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
        val mainOutput = objects.fileCollection().from(mainSourceSet.output)
        compileClasspath += mainOutput
        runtimeClasspath += mainOutput

        configurations.getByName(implementationConfigurationName)
            .extendsFrom(configurations.getByName(mainSourceSet.implementationConfigurationName))
    }
}

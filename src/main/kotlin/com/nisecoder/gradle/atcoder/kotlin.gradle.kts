package com.nisecoder.gradle.atcoder

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.nisecoder.gradle.atcoder.jvm")
    kotlin("jvm")
}

val compiler: Provider<JavaCompiler> = javaToolchains.compilerFor {
    languageVersion.set(JavaLanguageVersion.of(11))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        languageVersion = "1.3"
        jvmTarget = compiler.get().metadata.languageVersion.toString()
        javaParameters = true

        jdkHome = compiler.get().metadata.installationPath.asFile.absolutePath

        useIR = true
    }
}

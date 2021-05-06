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
val javaPluginExtension = extensions.getByType<JavaPluginExtension>()

atcoder.contestTask.all {
    val mainSourceSet = sourceSets.create(name) {
        val mainSourceSet = sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
        val mainOutput = objects.fileCollection().from(mainSourceSet.output)
        compileClasspath += mainOutput
        runtimeClasspath += mainOutput

        configurations.getByName(implementationConfigurationName)
            .extendsFrom(configurations.getByName(mainSourceSet.implementationConfigurationName))
    }

    val testSourceSet = sourceSets.create("${name}Test") {
        val testSourceSet = sourceSets[SourceSet.TEST_SOURCE_SET_NAME]
        val testOutput = objects.fileCollection().from(testSourceSet.output)
        compileClasspath += mainSourceSet.output + testOutput
        runtimeClasspath += mainSourceSet.output + testOutput

        configurations.getByName(implementationConfigurationName)
            .extendsFrom(configurations.getByName(testSourceSet.implementationConfigurationName))
    }

    val testTask = tasks.register<Test>("test$name") {
        description = "Runs the unit tests."
        group = JavaBasePlugin.VERIFICATION_GROUP

        testClassesDirs = testSourceSet.output.classesDirs
        classpath = testSourceSet.runtimeClasspath
        modularity.inferModulePath.convention(javaPluginExtension.modularity.inferModulePath)
    }

    tasks.named(JavaBasePlugin.CHECK_TASK_NAME) {
        dependsOn(testTask)
    }
}

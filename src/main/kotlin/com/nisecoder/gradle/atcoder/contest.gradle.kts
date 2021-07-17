package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderFetchTaskListTask
import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderSubmitTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val atcoder = extensions.create<AtCoderExtension>("atcoder")
atcoder.contestName.convention(project.name)

tasks {
    val atcoderLogin by rootProject.tasks.getting(AtCoderLoginTask::class)

    val fetchTaskListTask = register<AtCoderFetchTaskListTask>("atcoderFetchTaskList") {
        description = "Fetches task list for '${atcoder.contestName.get()}'"

        contestName.set(atcoder.contestName)
        sessionFile.set(atcoderLogin.sessionFile)

        taskListFile.set(project.buildDir.resolve("atcoder").resolve("tasks.tsv"))
    }

    register<AtCoderTaskListTask>("atcoderTaskList") {
        description = "Displays task list for '${atcoder.contestName.get()}'"

        taskListFile.set(fetchTaskListTask.flatMap { it.taskListFile })
    }

    atcoder.contestTask.all {
        val contestTaskName = name
        register<AtCoderSubmitTask>("atcoderSubmit$name") {
            description = "Submits '$contestTaskName' sourceCode"

            contestName.set(atcoder.contestName)
            taskId.set(contestTaskName)
            submitLanguage.set(language)
            sessionFile.set(atcoderLogin.sessionFile)
        }
    }
}

plugins.withType<JavaPlugin> {
    configure<JavaPluginExtension> {
        toolchain {
            // atcoder use openjdk 11.0.6
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    }

    val javaPluginExtension = extensions.getByType<JavaPluginExtension>()

    val sourceSets = extensions.getByType<SourceSetContainer>()

    atcoder.contestTask.all {
        val mainSourceSet = sourceSets.create(name) {
            val mainSourceSet = sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
            val mainOutput = objects.fileCollection().from(mainSourceSet.output)
            compileClasspath += mainOutput
            runtimeClasspath += mainOutput

            configurations.getByName(implementationConfigurationName)
                .extendsFrom(configurations.getByName(mainSourceSet.implementationConfigurationName))
        }

        val testTask = tasks.register<Test>("test$name") {
            description = "Runs the unit tests."
            group = JavaBasePlugin.VERIFICATION_GROUP

            testClassesDirs = mainSourceSet.output.classesDirs
            classpath = mainSourceSet.runtimeClasspath
            modularity.inferModulePath.convention(javaPluginExtension.modularity.inferModulePath)
        }

        tasks.named(JavaBasePlugin.CHECK_TASK_NAME) {
            dependsOn(testTask)
        }
    }
}

plugins.withType<KotlinPlatformJvmPlugin> {
    val javaToolchains = extensions.getByType<JavaToolchainService>()

    val compiler: Provider<JavaCompiler> = javaToolchains.compilerFor {
        // atcoder use openjdk 11.0.6
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // atcoder use 1.3.71
            languageVersion = "1.3"
            apiVersion = "1.3"

            jvmTarget = compiler.get().metadata.languageVersion.toString()
            javaParameters = true

            jdkHome = compiler.get().metadata.installationPath.asFile.absolutePath

            useIR = true
        }
    }
}

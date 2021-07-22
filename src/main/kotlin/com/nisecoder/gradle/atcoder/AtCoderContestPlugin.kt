package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderFetchTaskListTask
import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderSubmitTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AtCoderContestPlugin: Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(project: Project): Unit = project.run {
        val atcoder = extensions.create<AtCoderExtension>("atcoder")
        atcoder.contestName.convention(name)

        // register tasks
        val atcoderLogin = rootProject.tasks.named<AtCoderLoginTask>("atcoderLogin")

        val fetchTaskListTask = tasks.register<AtCoderFetchTaskListTask>("atcoderFetchTaskList") {
            description = "Fetches task list for '${atcoder.contestName.get()}'"

            contestName.set(atcoder.contestName)
            sessionFile.set(atcoderLogin.flatMap { it.sessionFile })

            taskListFile.set(buildDir.resolve("atcoder").resolve("tasks.tsv"))
        }

        tasks.register<AtCoderTaskListTask>("atcoderTaskList") {
            description = "Displays task list for '${atcoder.contestName.get()}'"

            taskListFile.set(fetchTaskListTask.flatMap { it.taskListFile })
        }

        // register sourceSets
        val defaultList = mutableListOf("A", "B", "C", "D", "E", "F", "G")
        atcoder.contestTasks.convention(defaultList)
        val contestTasks = objects.namedDomainObjectList(AtCoderContestTaskObject::class.java)
        afterEvaluate {
            contestTasks.addAll(atcoder.contestTasks.get().map { AtCoderContestTaskObject(it) })
        }

        contestTasks.all {
            val contestTaskName = name
            tasks.register<AtCoderSubmitTask>("atcoderSubmit$contestTaskName") {
                description = "Submits '$contestTaskName' sourceCode"

                contestName.set(atcoder.contestName)
                taskId.set(contestTaskName)
                submitLanguage.set(language)
                taskListFile.set(fetchTaskListTask.flatMap { it.taskListFile })
                sessionFile.set(atcoderLogin.flatMap { it.sessionFile })
            }
        }

        // configure for each language env
        plugins.withType<JavaPlugin> {
            val javaPluginExtension = extensions.getByType<JavaPluginExtension>().apply {
                toolchain {
                    // atcoder use openjdk 11.0.6
                    languageVersion.set(JavaLanguageVersion.of(11))
                }
            }

            val sourceSets = extensions.getByType<SourceSetContainer>()

            contestTasks.all {
                // copy settings from default "main" sourceSets
                val mainSourceSet = sourceSets.create(name) {
                    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
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
    }
}

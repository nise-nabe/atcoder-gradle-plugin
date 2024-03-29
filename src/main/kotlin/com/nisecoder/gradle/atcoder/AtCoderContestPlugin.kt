package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderFetchTaskListTask
import com.nisecoder.gradle.atcoder.task.AtCoderSessionTask
import com.nisecoder.gradle.atcoder.task.AtCoderSubmitTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask
import org.gradle.api.NamedDomainObjectList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("UnstableApiUsage")
class AtCoderContestPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        project.run {
            val atcoder = extensions.create<AtCoderExtension>("atcoder")
            atcoder.contestName.convention(name)

            val service =
                gradle.sharedServices.registerIfAbsent("atcoder", AtCoderBuildService::class) {
                    configureAtCoderService(this)
                }

            tasks.withType<AtCoderSessionTask>().configureEach {
                atcoderService.set(service)
            }

            val fetchTaskListTask =
                tasks.register<AtCoderFetchTaskListTask>("atcoderFetchTaskList") {
                    description = "Fetches task list for '${atcoder.contestName.get()}'"

                    contestName.set(atcoder.contestName)

                    taskListFile.set(layout.buildDirectory.file("atcoder/tasks.tsv"))
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
                    sourceCode.set("main.kt")
                }
            }

            // configure for each language env
            plugins.withType<JavaPlugin> {
                configureForJavaPlugin(contestTasks)
            }

            plugins.withType<KotlinPluginWrapper> {
                configureForKotlinJvmPlugin()
            }
        }

    private fun Project.configureForJavaPlugin(contestTasks: NamedDomainObjectList<AtCoderContestTaskObject>) {
        extensions.getByType<JavaPluginExtension>().apply {
            // atcoder use openjdk 17
            toolchain.languageVersion.convention(JavaLanguageVersion.of(17))
            toolchain.vendor.convention(JvmVendorSpec.ADOPTIUM)
        }

        val sourceSets = extensions.getByType<SourceSetContainer>()

        contestTasks.all {
            // copy settings from default "main" sourceSets
            val mainSourceSet =
                sourceSets.create(name) {
                    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                    val mainOutput = objects.fileCollection().from(mainSourceSet.output)
                    compileClasspath += mainOutput
                    runtimeClasspath += mainOutput

                    configurations.getByName(implementationConfigurationName)
                        .extendsFrom(configurations.getByName(mainSourceSet.implementationConfigurationName))
                }

            // create contest test task for each problem
            plugins.withType<JvmTestSuitePlugin> {
                configure<TestingExtension> {
                    suites.register("atcoderTest$name", JvmTestSuite::class) {
                        useJUnitJupiter()

                        dependencies {
                            implementation.add(project())
                            implementation.add(mainSourceSet.output)
                            implementation.add("org.jetbrains.kotlin:kotlin-test-junit5")
                        }

                        targets.all {
                            testTask.configure {
                                reports {
                                    junitXml.required.set(false)
                                    html.required.set(false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Project.configureForKotlinJvmPlugin() {
        configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                // java use 17 but kotlin jvm use 19
                languageVersion.set(JavaLanguageVersion.of(19))
            }
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                // atcoder use 1.8.20
                languageVersion.set(KotlinVersion.KOTLIN_1_8)
                apiVersion.set(KotlinVersion.KOTLIN_1_8)
                javaParameters.set(true)
            }
        }
    }
}

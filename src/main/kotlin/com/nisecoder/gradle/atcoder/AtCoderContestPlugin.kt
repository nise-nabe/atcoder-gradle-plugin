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
import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.`jvm-test-suite`
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension

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
            plugins.apply(JvmTestSuitePlugin::class)

            configure<JavaPluginExtension> {
                toolchain {
                    // atcoder use openjdk 11.0.6
                    languageVersion.set(JavaLanguageVersion.of(11))
                }
            }

            val sourceSets = extensions.getByType<SourceSetContainer>()

            contestTasks.all {
                // name will conflict with many objects
                val taskName = name
                // copy settings from default "main" sourceSets
                sourceSets.create(taskName) {
                    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                    val mainOutput = objects.fileCollection().from(mainSourceSet.output)
                    compileClasspath += mainOutput
                    runtimeClasspath += mainOutput

                    configurations.getByName(implementationConfigurationName)
                        .extendsFrom(configurations.getByName(mainSourceSet.implementationConfigurationName))
                }

                plugins.withType<JvmTestSuitePlugin> {
                    configure<TestingExtension> {
                        suites.register("$taskName-test", JvmTestSuite::class) {
                            useJUnitJupiter()
                            dependencies {
                                implementation(project)
                                implementation(sourceSets[taskName].output)
                            }
                        }
                    }
                }
            }
        }

    }
}

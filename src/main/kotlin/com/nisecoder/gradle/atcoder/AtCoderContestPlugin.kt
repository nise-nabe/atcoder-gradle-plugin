package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderFetchTaskListTask
import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderSubmitTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask
import org.gradle.api.Action
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
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AtCoderContestPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val atcoder = extensions.create<AtCoderExtension>("atcoder")
        atcoder.contestName.convention(name)

        val atcoderLogin = rootProject.tasks.named<AtCoderLoginTask>("atcoderLogin")

        val fetchTaskListTask = tasks.register<AtCoderFetchTaskListTask>("atcoderFetchTaskList") {
            description = "Fetches task list for '${atcoder.contestName.get()}'"

            contestName.set(atcoder.contestName)
            sessionFile.set(atcoderLogin.get().sessionFile)

            taskListFile.set(buildDir.resolve("atcoder").resolve("tasks.tsv"))
        }

        tasks.register<AtCoderTaskListTask>("atcoderTaskList") {
            description = "Displays task list for '${atcoder.contestName.get()}'"

            taskListFile.set(fetchTaskListTask.flatMap { it.taskListFile })
        }

        atcoder.contestTask.all(object : Action<AtCoderContestTaskObject> {
            override fun execute(config: AtCoderContestTaskObject) {
                val contestTaskName = config.name
                tasks.register("atcoderSubmit$contestTaskName", AtCoderSubmitTask::class.java, object : Action<AtCoderSubmitTask> {
                    override fun execute(task: AtCoderSubmitTask) {
                        task.description = "Submits '$contestTaskName' sourceCode"

                        task.contestName.set(atcoder.contestName)
                        task.taskId.set(contestTaskName)
                        task.submitLanguage.set(config.language)
                        task.sessionFile.set(atcoderLogin.get().sessionFile)
                    }
                })
            }
        })

        plugins.withType<JavaPlugin> {
            val javaPluginExtension = extensions.getByType<JavaPluginExtension>().apply {
                toolchain {
                    // atcoder use openjdk 11.0.6
                    languageVersion.set(JavaLanguageVersion.of(11))
                }
            }

            val sourceSets = extensions.getByType<SourceSetContainer>()

            atcoder.contestTask.all {
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

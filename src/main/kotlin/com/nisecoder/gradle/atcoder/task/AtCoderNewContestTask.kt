package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderNewContestTask: AtCoderTask() {
    @get:Input
    @set:Option(option = "contest", description = "contest name")
    abstract var contestName: String

    @get:Internal
    abstract val atcoderService: Property<AtCoderBuildService>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun newContest() {
        val contestDir = outputDir.get().asFile.resolve(contestName)
        if (!contestDir.exists()) contestDir.mkdir()

        val fetcher = AtCoderFetcher(atcoderService.get().login())
        val problems = fetcher.fetchTaskList(contestName).tasks.map { it.taskId }

        contestDir.resolve("build.gradle.kts").let { buildscriptFile ->
            if (!buildscriptFile.exists()) {
                // language=gradle.kts
                buildscriptFile.writeText(
                    """
                        plugins {
                            id("com.nisecoder.gradle.atcoder.contest")
                            id("com.nisecoder.gradle.atcoder.contest.kotlin")
                        }
                    """.trimIndent()
                )
            }
        }



        problems.forEach { problemName ->
            val problemDir = contestDir.resolve("src/$problemName/kotlin")
            if (!problemDir.exists()) problemDir.mkdirs()

            problemDir.resolve("main.kt").let { mainFile ->
                if (!mainFile.exists()) {
                    // language=kotlin
                    mainFile.writeText(
                    """
                        fun main() {
                        
                        }
                    """.trimIndent()
                    )
                }
            }
        }
    }
}

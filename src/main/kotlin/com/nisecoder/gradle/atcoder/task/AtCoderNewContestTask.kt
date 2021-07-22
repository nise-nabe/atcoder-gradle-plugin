package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import com.nisecoder.gradle.atcoder.internal.readFirstLine
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderNewContestTask: AtCoderTask() {
    @get:Input
    @set:Option(option = "contest", description = "contest name")
    abstract var contestName: String

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun newContest() {
        val contestDir = outputDir.get().asFile.resolve(contestName)
        if (!contestDir.exists()) contestDir.mkdir()

        val fetcher = AtCoderFetcher(sessionFile.get().readFirstLine())
        val problems = fetcher.fetchTaskList(contestName).tasks.map { it.taskId }

        contestDir.resolve("build.gradle.kts").let { buildscriptFile ->
            if (!buildscriptFile.exists()) {
                // language=gradle.kts
                buildscriptFile.writeText(
                    """
                        plugins {
                            kotlin("jvm") version "1.3.71"
                            id("com.nisecoder.gradle.atcoder.contest")
                        }
                    """.trimIndent()
                )
            }
        }



        problems.forEach { problemName ->
            val problemDir = contestDir.resolve("src/$problemName/kotlin")
            if (!problemDir.exists()) problemDir.mkdirs()

            val mainFile = problemDir.resolve("main.kt")
            if (!mainFile.exists()) mainFile.createNewFile()
        }
    }
}

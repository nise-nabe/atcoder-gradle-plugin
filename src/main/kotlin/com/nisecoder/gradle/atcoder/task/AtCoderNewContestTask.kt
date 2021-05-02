package com.nisecoder.gradle.atcoder.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderNewContestTask: DefaultTask() {
    @get:Input
    @set:Option(option = "contest", description = "contest name")
    abstract var contestName: String

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun newContest() {
        val contestDir = outputDir.get().asFile.resolve(contestName)
        if (!contestDir.exists()) contestDir.mkdir()

        val problems = listOf("A", "B", "C", "D", "E", "F")

        val problemString = problems.joinToString(prefix = "\"", postfix = "\"", separator = "\", \"")

        contestDir.resolve("build.gradle.kts").let { buildscriptFile ->
            if (!buildscriptFile.exists()) {
                // language=gradle.kts
                buildscriptFile.writeText(
                    """
                        plugins {
                            id("com.nisecoder.gradle.atcoder.contest")
                            id("com.nisecoder.gradle.atcoder.kotlin")
                        }

                        atcoder {
                            problems.set(listOf(${problemString}))
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

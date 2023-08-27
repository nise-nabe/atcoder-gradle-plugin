package com.nisecoder.gradle.atcoder.language

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

internal class AtCoderContestKotlinPluginFunctionalTest {
    @Test
    fun apply(@TempDir tempDir: Path) {
        val settingsFile: File = tempDir.resolve("settings.gradle.kts").toFile()
        // create root-project directory
        val rootBuildFile: File = tempDir.resolve("build.gradle.kts").toFile()
        // create sub-project directory
        tempDir.resolve("sample").toFile().mkdir()
        // create sub-project buildscript
        val buildFile: File = tempDir.resolve("sample/build.gradle.kts").toFile()

        // language=gradle.kts
        settingsFile.writeText(
            """
            | rootProject.name = "test-project"
            | include("sample")
            | plugins {
            |   id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
            | }
            """.trimMargin()
        )

        // language=gradle.kts
        rootBuildFile.writeText(
            """
            | plugins {
            |   id("com.nisecoder.gradle.atcoder")
            | }     
            """.trimMargin()
        )

        // language=gradle.kts
        buildFile.writeText(
            """
            | plugins {
            |   id("com.nisecoder.gradle.atcoder.contest")
            |   id("com.nisecoder.gradle.atcoder.contest.kotlin")
            | }
            """.trimMargin()
        )

        val runner = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("help")
            .withProjectDir(tempDir.toFile())

        val buildResult = runner.build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":help")?.outcome)
    }
}

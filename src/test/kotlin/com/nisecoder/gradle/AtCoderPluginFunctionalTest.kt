package com.nisecoder.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.assertEquals

internal class AtCoderPluginFunctionalTest {
    @Test
    fun apply(
        @TempDir tempDir: Path,
    ) {
        val settingsFile: File = tempDir.resolve("settings.gradle.kts").toFile()
        val buildFIle: File = tempDir.resolve("build.gradle.kts").toFile()

        // language=gradle.kts
        settingsFile.writeText(
            """
            | rootProject.name = "test-project"
            | 
            | plugins {
            |   id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
            | }
            """.trimMargin(),
        )

        // language=gradle.kts
        buildFIle.writeText(
            """
            | plugins {
            |   id("com.nisecoder.gradle.atcoder")
            | }
            """.trimMargin(),
        )

        val runner =
            GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("help")
                .withProjectDir(tempDir.toFile())

        val buildResult = runner.build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":help")?.outcome)
    }
}

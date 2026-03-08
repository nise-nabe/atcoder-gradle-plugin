package com.nisecoder.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals

internal class AtCoderPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: Path

    private val rootBuildFile by lazy { projectDir.resolve("build.gradle.kts") }

    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    @Test
    fun apply() {
        // language=gradle.kts
        settingsFile.writeText(
            """
            | rootProject.name = "test-project"
            | 
            | plugins {
            |   id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
            | }
            """.trimMargin(),
        )

        // language=gradle.kts
        rootBuildFile.writeText(
            """
            | plugins {
            |   id("com.nisecoder.gradle.atcoder")
            | }
            """.trimMargin(),
        )

        val runner =
            GradleRunner
                .create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("help")
                .withProjectDir(projectDir.toFile())

        val buildResult = runner.build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":help")?.outcome)
    }
}

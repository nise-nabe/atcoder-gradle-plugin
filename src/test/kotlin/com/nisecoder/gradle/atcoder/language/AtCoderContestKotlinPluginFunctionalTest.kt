package com.nisecoder.gradle.atcoder.language

import com.nisecoder.gradle.GradleVersionProvider
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.io.File
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals

internal class AtCoderContestKotlinPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: Path

    private val rootBuildFile by lazy { projectDir.resolve("build.gradle.kts") }

    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    @ParameterizedTest
    @ArgumentsSource(GradleVersionProvider::class)
    fun apply(gradleVersion: String) {
        projectDir.resolve("sample").toFile().mkdir()
        // create sub-project buildscript
        val buildFile: File = projectDir.resolve("sample/build.gradle.kts").toFile()

        // language=gradle.kts
        settingsFile.writeText(
            """
            | rootProject.name = "test-project"
            | include("sample")
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

        // language=gradle.kts
        buildFile.writeText(
            """
            | plugins {
            |   id("com.nisecoder.gradle.atcoder.contest")
            |   id("com.nisecoder.gradle.atcoder.contest.kotlin")
            | }
            """.trimMargin(),
        )

        val runner =
            GradleRunner
                .create()
                .forwardOutput()
                .withPluginClasspath()
                .withGradleVersion(gradleVersion)
                .withArguments("help")
                .withProjectDir(projectDir.toFile())

        val buildResult = runner.build()

        assertEquals(TaskOutcome.SUCCESS, buildResult.task(":help")?.outcome)
    }
}

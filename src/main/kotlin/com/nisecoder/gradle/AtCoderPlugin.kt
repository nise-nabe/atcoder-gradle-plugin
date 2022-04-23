package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import com.nisecoder.gradle.atcoder.configureAtCoderService
import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent


@Suppress("UnstableApiUsage")
class AtCoderPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val service = gradle.sharedServices.registerIfAbsent("atcoder", AtCoderBuildService::class) {
            configureAtCoderService(this)
        }

        tasks.register<AtCoderLoginTask>("atcoderLogin") {
            description = "Logins to AtCoder using credentials"

            atcoderService.set(service)
            sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
        }

        tasks.register<AtCoderNewContestTask>("atcoderNew") {
            description = "Creates AtCoder Contest Project"

            atcoderService.set(service)
            outputDir.set(project.rootDir.resolve("subprojects/contests/"))
        }
    }
}

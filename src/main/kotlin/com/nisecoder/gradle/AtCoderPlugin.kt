package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import com.nisecoder.gradle.atcoder.configureAtCoderService
import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import com.nisecoder.gradle.atcoder.task.AtCoderSessionTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.withType

class AtCoderPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val service = gradle.sharedServices.registerIfAbsent("atcoder", AtCoderBuildService::class) {
            configureAtCoderService(this)
        }

        tasks.withType<AtCoderSessionTask>().configureEach {
            atcoderService.set(service)
        }

        tasks.register<AtCoderLoginTask>("atcoderLogin") {
            description = "Logins to AtCoder using credentials"
        }

        tasks.register<AtCoderNewContestTask>("atcoderNew") {
            description = "Creates AtCoder Contest Project"

            outputDir.set(project.rootDir.resolve("subprojects/contests/"))
        }
    }
}

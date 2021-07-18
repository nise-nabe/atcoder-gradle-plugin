package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.register


class AtCoderPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val atcoderLogin = tasks.register<AtCoderLoginTask>("atcoderLogin") {
            description = "Logins to AtCoder using credentials"

            credentials.set(providers.credentials(PasswordCredentials::class, "atcoder"))
            sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
        }

        tasks.register<AtCoderNewContestTask>("atcoderNew") {
            description = "Creates AtCoder Contest Project"

            sessionFile.set(atcoderLogin.flatMap { it.sessionFile })
            outputDir.set(project.rootDir.resolve("subprojects/contests/"))
        }
    }
}

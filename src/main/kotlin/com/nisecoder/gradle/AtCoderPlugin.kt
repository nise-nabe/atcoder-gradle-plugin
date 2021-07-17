package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.kotlin.dsl.credentials


class AtCoderPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        val atcoderLogin = tasks.register("atcoderLogin", AtCoderLoginTask::class.java, object : Action<AtCoderLoginTask> {
            override fun execute(task: AtCoderLoginTask) {
                task.description = "Logins to AtCoder using credentials"

                val credentials = providers.credentials(PasswordCredentials::class, "atcoder")
                task.credentials.set(credentials)
                task.sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
            }
        })

        tasks.register("atcoderNew", AtCoderNewContestTask::class.java, object : Action<AtCoderNewContestTask> {
            override fun execute(task: AtCoderNewContestTask) {
                task.description = "Creates AtCoder Contest Project"

                task.sessionFile.set(atcoderLogin.flatMap { it.sessionFile })
                task.outputDir.set(project.rootDir.resolve("subprojects/contests/"))
            }
        })
    }
}

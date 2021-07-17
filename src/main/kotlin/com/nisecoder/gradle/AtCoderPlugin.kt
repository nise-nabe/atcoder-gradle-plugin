package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project


class AtCoderPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        plugins.apply("nu.studer.credentials")

        val atcoderLogin = tasks.register("atcoderLogin", AtCoderLoginTask::class.java, object : Action<AtCoderLoginTask> {
            override fun execute(task: AtCoderLoginTask) {
                task.description = "Logins to AtCoder using credentials"

                val credentials: CredentialsContainer =
                    rootProject.extensions.extraProperties.get("credentials") as CredentialsContainer
                task.username.set(credentials.getProperty("atcoder.username").toString())
                task.password.set(credentials.getProperty("atcoder.password").toString())
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

package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    id("nu.studer.credentials")
}

tasks {
    val atcoderLogin = register<AtCoderLoginTask>("atcoderLogin") {
        description = "Logins to AtCoder using credentials"

        val credentials: CredentialsContainer by rootProject.extra
        username.set(credentials.getProperty("atcoder.username").toString())
        password.set(credentials.getProperty("atcoder.password").toString())
        sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
    }

    register<AtCoderNewContestTask>("atcoderNew") {
        description = "Creates AtCoder Contest Project"

        sessionFile.set(atcoderLogin.flatMap { sessionFile })
        outputDir.set(project.rootDir.resolve("subprojects/contests/"))
    }
}

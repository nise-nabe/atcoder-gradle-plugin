package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import nu.studer.gradle.credentials.domain.CredentialsContainer

plugins {
    id("nu.studer.credentials")
}

tasks {
    register<AtCoderLoginTask>("atcoderLogin") {
        val credentials: CredentialsContainer by rootProject.extra
        username = credentials.getProperty("atcoder.username").toString()
        password = credentials.getProperty("atcoder.password").toString()
        sessionFile.set(project.buildDir.resolve("atcoder/session.txt"))
    }

    register<AtCoderNewContestTask>("atcoderNew") {
        outputDir.set(project.rootDir.resolve("subprojects/contests/"))
    }
}

package com.nisecoder.gradle

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask

plugins {
    id("com.nisecoder.gradle.atcoder.credentials")
}

tasks {
    val atcoderLogin by getting(AtCoderLoginTask::class)

    register<AtCoderNewContestTask>("atcoderNew") {
        description = "Creates AtCoder Contest Project"

        sessionFile.set(atcoderLogin.sessionFile)
        outputDir.set(project.rootDir.resolve("subprojects/contests/"))
    }
}

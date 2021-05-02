package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask

plugins {
    id("com.nisecoder.gradle.atcoder.credentials")
}

tasks {
    val atcoderLogin by getting(AtCoderLoginTask::class)

    register<AtCoderNewContestTask>("atcoderNew") {
        outputDir.set(project.rootDir.resolve("subprojects/contests/"))
    }

    register<AtCoderTaskListTask>("atcoderTasks") {
        sessionFile.set(atcoderLogin.sessionFile)
    }
}

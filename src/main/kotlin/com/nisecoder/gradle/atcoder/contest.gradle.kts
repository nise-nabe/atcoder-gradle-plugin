package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask


plugins {
    java
    id("com.nisecoder.gradle.atcoder.credentials")
}

val atcoder = extensions.create<AtCoderExtension>("atcoder")
atcoder.contestName.convention(project.name)

tasks {
    val atcoderLogin by getting(AtCoderLoginTask::class)

    register<AtCoderTaskListTask>("atcoderTasks") {
        contestName = atcoder.contestName.get()
        sessionFile.set(atcoderLogin.sessionFile)
    }
}

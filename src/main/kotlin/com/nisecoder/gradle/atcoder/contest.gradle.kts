package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import com.nisecoder.gradle.atcoder.task.AtCoderSubmitTask
import com.nisecoder.gradle.atcoder.task.AtCoderTaskListTask


plugins {
    java
    id("com.nisecoder.gradle.atcoder.credentials")
}

val atcoder = extensions.create<AtCoderExtension>("atcoder")
atcoder.contestName.convention(project.name)

tasks {
    val atcoderLogin by rootProject.tasks.getting(AtCoderLoginTask::class)

    register<AtCoderTaskListTask>("atcoderTaskList") {
        contestName.set(atcoder.contestName)
        sessionFile.set(atcoderLogin.sessionFile)
    }

    register<AtCoderSubmitTask>("atcoderSubmit") {
        contestName.set(atcoder.contestName)
        sessionFile.set(atcoderLogin.sessionFile)
    }
}

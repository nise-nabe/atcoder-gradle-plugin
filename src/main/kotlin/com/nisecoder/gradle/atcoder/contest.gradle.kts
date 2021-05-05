package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderFetchTaskListTask
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

    val fetchTaskListTask = register<AtCoderFetchTaskListTask>("atcoderTaskList") {
        description = "Fetches task list for '${atcoder.contestName.get()}'"

        contestName.set(atcoder.contestName)
        sessionFile.set(atcoderLogin.sessionFile)

        taskListFile.set(project.buildDir.resolve("atcoder").resolve("tasks.tsv"))
    }

    register<AtCoderTaskListTask>("atcoderTaskList") {
        description = "Displays task list for '${atcoder.contestName.get()}'"

        taskListFile.set(fetchTaskListTask.flatMap { it.taskListFile })
    }

    atcoder.contestTask.all {
        val contestTaskName = name
        register<AtCoderSubmitTask>("atcoderSubmit$name") {
            description = "Submits '$contestTaskName' sourceCode"

            contestName.set(atcoder.contestName)
            taskId.set(contestTaskName)
            sessionFile.set(atcoderLogin.sessionFile)
        }
    }
}

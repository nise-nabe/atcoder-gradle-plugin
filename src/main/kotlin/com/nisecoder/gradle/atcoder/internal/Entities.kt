package com.nisecoder.gradle.atcoder.internal


class ContestTaskList {
    lateinit var tasks: List<ContestTask>
}

data class ContestTask(
    val taskId: String,
    val taskName: String,
    val timeLimit: String,
    val memoryLimit: String,
    val taskScreenName: String,
)

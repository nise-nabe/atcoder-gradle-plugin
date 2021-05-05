package com.nisecoder.gradle.atcoder.internal


class ContestTaskList {
    lateinit var tasks: List<ContestTask>

    companion object
}

data class ContestTask(
    val taskId: String,
    val taskName: String,
    val timeLimit: String,
    val memoryLimit: String,
    val taskScreenName: String,
) {
    fun toTsvRow(): String {
        return listOf(
            taskId,
            taskName,
            timeLimit,
            memoryLimit,
            taskScreenName
        ).joinToString( "\t")
    }

    companion object {
        fun fromTsvRow(row: String): ContestTask {
            return row.split("\t").let {
                ContestTask(
                    taskId = it[0],
                    taskName = it[1],
                    timeLimit = it[2],
                    memoryLimit = it[3],
                    taskScreenName = it[4],
                )
            }
        }
    }
}

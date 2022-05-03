package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.ContestTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderTaskListTask : AtCoderTask() {
    @get:InputFile
    abstract val taskListFile: RegularFileProperty

    @TaskAction
    fun taskList() {
        taskListFile.get().asFile.readLines().map(ContestTask::fromTsvRow).forEach {
            logger.lifecycle("${it.taskId}: ${it.taskName}")
        }
    }
}

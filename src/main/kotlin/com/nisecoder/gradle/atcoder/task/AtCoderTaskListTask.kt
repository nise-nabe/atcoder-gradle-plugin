package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.ContestTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "temporary")
abstract class AtCoderTaskListTask : AtCoderTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val taskListFile: RegularFileProperty

    @TaskAction
    fun taskList() {
        taskListFile.get().asFile.readLines().map(ContestTask::fromTsvRow).forEach {
            logger.lifecycle("${it.taskId}: ${it.taskName}")
        }
    }
}

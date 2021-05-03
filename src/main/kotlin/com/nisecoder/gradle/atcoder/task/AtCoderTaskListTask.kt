package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderTaskListTask: AtCoderTask() {
    @get:Input
    @set:Option(option = "contest", description = "contest name")
    abstract var contestName: String

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @TaskAction
    fun taskList() {
        val session = sessionFile.get().asFile.readLines().first()
        val fetcher = AtCoderFetcher(session)

        val result = fetcher.fetchTaskList(contestName)
        result.tasks.forEach {
            logger.lifecycle("${it.taskId}: ${it.taskName}")
        }
    }
}

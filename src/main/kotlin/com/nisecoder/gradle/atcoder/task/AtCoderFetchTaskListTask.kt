package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderFetchTaskListTask: AtCoderSessionTask() {
    @get:Input
    abstract val contestName: Property<String>

    @get:OutputFile
    abstract val taskListFile: RegularFileProperty

    @TaskAction
    fun taskList() {
        val fetcher = AtCoderFetcher(atcoderService.get().login())

        val result = fetcher.fetchTaskList(contestName.get())
        taskListFile.get().asFile.printWriter().use { writer ->
            result.tasks.forEach {
                writer.println(it.toTsvRow())
            }
        }
    }
}

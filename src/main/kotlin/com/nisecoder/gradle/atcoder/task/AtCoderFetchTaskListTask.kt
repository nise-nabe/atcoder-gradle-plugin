package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import com.nisecoder.gradle.atcoder.internal.readFirstLine
import io.ktor.util.*
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderFetchTaskListTask: AtCoderTask() {
    @get:Input
    abstract val contestName: Property<String>

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @get:OutputFile
    abstract val taskListFile: RegularFileProperty

    @KtorExperimentalAPI
    @TaskAction
    fun taskList() {
        val session = sessionFile.get().readFirstLine()
        val fetcher = AtCoderFetcher(session)

        val result = fetcher.fetchTaskList(contestName.get())
        taskListFile.get().asFile.printWriter().use { writer ->
            result.tasks.forEach {
                writer.println(it.toTsvRow())
            }
        }
    }
}

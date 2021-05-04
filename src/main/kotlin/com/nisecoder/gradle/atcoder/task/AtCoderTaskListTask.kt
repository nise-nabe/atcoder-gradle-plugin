package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
import io.ktor.util.KtorExperimentalAPI
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderTaskListTask: AtCoderTask() {
    @get:Input
    abstract val contestName: Property<String>

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @KtorExperimentalAPI
    @TaskAction
    fun taskList() {
        val session = sessionFile.get().asFile.readLines().first()
        val fetcher = AtCoderFetcher(session)

        val result = fetcher.fetchTaskList(contestName.get())
        result.tasks.forEach {
            logger.lifecycle("${it.taskId}: ${it.taskName}")
        }
    }
}

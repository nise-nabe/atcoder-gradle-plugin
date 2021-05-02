package com.nisecoder.gradle.atcoder.task

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
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

        val result = skrape(HttpFetcher) {
            request {
                url = "https://atcoder.jp/contests/${contestName}/tasks"
                cookies = mapOf("REVEL_SESSION" to session)
                headers = mapOf("Accept-Language" to "ja")
            }

            extractIt<ContestTasks> {
                htmlDocument {
                    it.tasks = tbody { tr { findAll {
                        map { it.td {
                            ContestTask(
                                taskId = findByIndex(0) { text },
                                taskName = findByIndex(1) { text },
                                timeLimit = findByIndex(2) { text },
                                memoryLimit = findByIndex(3) { text }
                            ) } }
                    } } }
                }
            }
        }
        result.tasks.forEach {
            logger.lifecycle("${it.taskId}: ${it.taskName}")
        }
    }
}

class ContestTasks {
    lateinit var tasks: List<ContestTask>
}

data class ContestTask(
    val taskId: String,
    val taskName: String,
    val timeLimit: String,
    val memoryLimit: String,
)

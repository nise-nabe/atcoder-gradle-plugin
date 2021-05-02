package com.nisecoder.gradle.atcoder.internal

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr

class AtCoderFetcher(private val session: String) {
    fun fetchTaskList(contestName: String, ): ContestTaskList {
        return skrape(HttpFetcher) {
            request {
                url = "${AtCoderSite.baseUrl}/contests/${contestName}/tasks"
                cookies = mapOf(AtCoderSite.sessionName to session)
                headers = mapOf("Accept-Language" to "ja")
            }

            extractIt {
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
    }
}

package com.nisecoder.gradle.atcoder.internal

import it.skrape.core.document
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.tbody
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr

class AtCoderFetcher(private val session: String) {
    fun fetchTaskList(contestName: String): ContestTaskList {
        return skrape(HttpFetcher) {
            request {
                url = "${AtCoderSite.BASE_URL}/contests/$contestName/tasks"
                headers =
                    mapOf(
                        "Accept-Language" to "ja",
                        "Cookie" to session.cookieValue(),
                    )
            }

            extractIt {
                if (responseStatus.code == 404 && document.wholeText.contains("権限がありません")) {
                    throw AtCoderUnauthorizedException("not login")
                }
                htmlDocument {
                    it.tasks =
                        tbody {
                            tr {
                                findAll {
                                    map {
                                        it.td {
                                            ContestTask(
                                                taskId = findByIndex(0) { text },
                                                taskName = findByIndex(1) { text },
                                                timeLimit = findByIndex(2) { text },
                                                memoryLimit = findByIndex(3) { text },
                                                taskScreenName =
                                                    findByIndex(4) {
                                                        eachHref.first().split("taskScreenName=")[1]
                                                    },
                                                taskUrl = findByIndex(0) { AtCoderSite.BASE_URL + eachHref.first() },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

package com.nisecoder.gradle.atcoder.internal

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking

class AtCoderFetcher(
    private val session: String,
) {
    fun fetchTaskList(contestName: String): ContestTaskList =
        runBlocking {
            HttpClient(CIO).use { client ->
                val response =
                    client.get("${AtCoderSite.BASE_URL}/contests/$contestName/tasks") {
                        header(HttpHeaders.AcceptLanguage, "ja")
                        header(HttpHeaders.Cookie, session.cookieValue())
                    }

                val html = response.bodyAsText()
                if (response.status == HttpStatusCode.NotFound && html.contains("権限がありません")) {
                    throw AtCoderUnauthorizedException("not login")
                }

                val document = Ksoup.parse(html)
                val rows = document.select("tbody tr")
                val tasks =
                    rows.map { tr ->
                        val tds = tr.select("td")
                        ContestTask(
                            taskId = tds[0].text(),
                            taskName = tds[1].text(),
                            timeLimit = tds[2].text(),
                            memoryLimit = tds[3].text(),
                            taskScreenName =
                                tds[4]
                                    .select("a")
                                    .first()
                                    ?.attr("href")
                                    ?.substringAfter("taskScreenName=") ?: "",
                            taskUrl = AtCoderSite.BASE_URL + (tds[0].select("a").first()?.attr("href") ?: ""),
                        )
                    }
                ContestTaskList().apply { this.tasks = tasks }
            }
        }
}

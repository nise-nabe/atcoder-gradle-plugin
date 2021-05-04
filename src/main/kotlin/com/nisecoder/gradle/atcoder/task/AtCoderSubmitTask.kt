package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import com.nisecoder.gradle.atcoder.internal.csrfToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.renderCookieHeader
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderSubmitTask: AtCoderTask() {
    @get:Input
    @set:Option(option = "contest", description = "contest name")
    abstract var contestName: String

    @get:Input
    @set:Option(option = "task", description = "contest task")
    abstract var taskScreenName: String

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @TaskAction
    fun submit() {
        val session = sessionFile.get().asFile.readLines().first()

        val submitUrl = "${AtCoderSite.contest}/$contestName/submit"
        runBlocking {
            val client = HttpClient(CIO) {
                expectSuccess = false
                followRedirects = false
            }
            val response: HttpResponse = client.submitForm(
                url = submitUrl,
                formParameters = Parameters.build {
                    append("data.TaskScreenName", taskScreenName)
                    append("data.LanguageId", "4032")
                    append("sourceCode", """
                        fun main() {
                            val a = readLine()!!.toInt()
                            val (b, c) = readLine()!!.split(" ").map { it.toInt() }
                            val s = readLine()!!

                            println("${'$'}{a + b + c} ${'$'}s")
                        }
                    """.trimIndent())
                    append("csrf_token", session.csrfToken())
                },
                encodeInQuery = false
            ) {
                header(HttpHeaders.AcceptLanguage, "ja")
                header(
                    HttpHeaders.Cookie, Cookie(
                    name = AtCoderSite.sessionName,
                    value = session,
                    encoding = CookieEncoding.RAW
                ).let(::renderCookieHeader))
            }
        }


    }
}

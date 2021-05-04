package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderException
import com.nisecoder.gradle.atcoder.internal.AtCoderFetcher
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
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType

abstract class AtCoderSubmitTask: AtCoderTask() {
    @get:Input
    abstract var contestName: Property<String>

    @get:Input
    abstract var taskId: Property<String>

    @get:InputFile
    abstract val sessionFile: RegularFileProperty

    @KtorExperimentalAPI
    @TaskAction
    fun submit() {
        val session = sessionFile.get().asFile.readLines().first()

        val task = AtCoderFetcher(session).fetchTaskList(contestName.get()).tasks.first { it.taskId == taskId.get() }

        val sourceSets: SourceSetContainer = project.extensions.getByType()

        val submitFile = sourceSets.getAt(task.taskId).allSource.find { it.name == "main.kt" }
            ?: throw AtCoderException("cannot find file for submit")

        val sourceCode = submitFile.readText()

        runBlocking {
            val client = HttpClient(CIO) {
                expectSuccess = false
                followRedirects = false
            }
            client.submitForm<HttpResponse>(
                url = "${AtCoderSite.contest}/$contestName/submit",
                formParameters = Parameters.build {
                    append("data.TaskScreenName", task.taskScreenName)
                    append("data.LanguageId", "4032")
                    append("sourceCode", sourceCode)
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

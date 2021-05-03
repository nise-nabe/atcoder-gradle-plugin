package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderException
import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import com.nisecoder.gradle.atcoder.internal.AtCoderUnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.util.*
import it.skrape.fetcher.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

abstract class AtCoderLoginTask : AtCoderTask() {
    @get:Input
    @set:Option(option = "username", description = "AtCoder Username")
    abstract var username: String

    @get:Input
    @set:Option(option = "password", description = "AtCoder Password")
    abstract var password: String

    @get:OutputFile
    abstract val sessionFile: RegularFileProperty

    @KtorExperimentalAPI
    @TaskAction
    fun login() {
        val session = skrape(HttpFetcher) {
            request {
                url = AtCoderSite.home
            }

            extract {
               cookies.first { it.name == AtCoderSite.sessionName }
            }
        }

        val csrfToken = session.value.split("%00")
            .first { it.startsWith("csrf_token") }
            .decodeURLQueryComponent()
            .split(":")[1]

        runBlocking {
            val client = HttpClient(CIO) {
                expectSuccess = false
                followRedirects = false
            }
            val response: HttpResponse = client.submitForm(
                url = AtCoderSite.login,
                formParameters = Parameters.build {
                    append("username", username)
                    append("password", password)
                    append("csrf_token", csrfToken)
                },
                encodeInQuery = false
            ) {
                header(HttpHeaders.AcceptLanguage, "ja")
                header(HttpHeaders.Cookie, Cookie(
                    name = AtCoderSite.sessionName,
                    value = session.value,
                    encoding = CookieEncoding.RAW
                ).let(::renderCookieHeader))
            }
            when (response.status) {
                OK, Found -> return@runBlocking
                Forbidden -> throw AtCoderUnauthorizedException(response.readText())
                else -> throw AtCoderException(response.status.toString())
            }
        }

        sessionFile.asFile.get().writeText(session.value)

        logger.lifecycle("login user: $username")
    }
}


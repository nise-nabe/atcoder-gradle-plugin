package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderException
import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import com.nisecoder.gradle.atcoder.internal.AtCoderUnauthorizedException
import com.nisecoder.gradle.atcoder.internal.cookieValue
import com.nisecoder.gradle.atcoder.internal.csrfToken
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
import it.skrape.fetcher.*
import kotlinx.coroutines.runBlocking
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderLoginTask : AtCoderTask() {
    @get:Internal
    abstract val credentials: Property<PasswordCredentials>

    @get:OutputFile
    abstract val sessionFile: RegularFileProperty

    @TaskAction
    fun login() {
        val (username, password) = credentials.get().let { it.username to it.password }

        if (username == null || password == null) {
            throw Exception("username and password is required")
        }

        val session = skrape(HttpFetcher) {
            request {
                url = AtCoderSite.home
            }

            response {
               cookies.first { it.name == AtCoderSite.sessionName }
            }
        }

        val csrfToken = session.value.csrfToken()

        val loginSession = runBlocking {
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
                header(HttpHeaders.Cookie, session.value.cookieValue())
            }
            when (response.status) {
                OK, Found -> return@runBlocking response.setCookie().first { it.name == AtCoderSite.sessionName }.value
                Forbidden -> throw AtCoderUnauthorizedException(response.readText())
                else -> throw AtCoderException(response.status.toString())
            }
        }

        sessionFile.asFile.get().writeText(loginSession)

        logger.lifecycle("login user: $username")
    }
}

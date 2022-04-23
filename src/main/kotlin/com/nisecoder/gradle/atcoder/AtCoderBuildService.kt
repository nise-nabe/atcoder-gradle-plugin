package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.internal.AtCoderException
import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import com.nisecoder.gradle.atcoder.internal.AtCoderUnauthorizedException
import com.nisecoder.gradle.atcoder.internal.cookieValue
import com.nisecoder.gradle.atcoder.internal.csrfToken
import com.nisecoder.gradle.atcoder.internal.readFirstLine
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode.Companion.Forbidden
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.Parameters
import io.ktor.http.setCookie
import it.skrape.fetcher.Cookie
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.runBlocking
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

@Suppress("UnstableApiUsage")
abstract class AtCoderBuildService: BuildService<AtCoderBuildService.Params> {
    interface Params: BuildServiceParameters {
        val credentials: Property<PasswordCredentials>
        val isPersistence: Property<Boolean>
        val sessionFile: RegularFileProperty
    }

    val username: String?
        get() = parameters.credentials.get().username

    private val session: String by lazy {
        if (parameters.isPersistence.get() && parameters.sessionFile.get().asFile.exists()) {
            parameters.sessionFile.get().readFirstLine()
        } else {
            val (username, password) = parameters.credentials.get().let { it.username to it.password }

            if (username == null || password == null) {
                throw Exception("username and password is required")
            }

            val anonymous = fetchAnonymousCookie()

            loginInternal(anonymous, username, password).also {
                if (parameters.isPersistence.get()) {
                    parameters.sessionFile.asFile.get().writeText(it)
                }
            }
        }
    }

    /**
     * Login to AtCoder using the credentials provided by the user.
     *
     * @return login Session ID
     * @throws AtCoderUnauthorizedException if the credentials is incorrect
     */
    fun login(): String {
        return session
    }

    private fun fetchAnonymousCookie(): Cookie {
        val session = skrape(HttpFetcher) {
            request {
                url = AtCoderSite.home
            }

            response {
                cookies.first { it.name == AtCoderSite.sessionName }
            }
        }

        return session
    }

    /**
     * @param session AtCoder Site Session
     * @param username AtCoder User Name
     * @param password AtCoder User Password
     *
     * @return login Session ID
     *
     * @throws AtCoderUnauthorizedException if the [username] or [password] is incorrect
     */
    private fun loginInternal(session: Cookie, username: String, password: String): String {
        return runBlocking {
            val client = HttpClient(CIO) {
                expectSuccess = false
                followRedirects = false
            }
            val response: HttpResponse = client.submitForm(
                url = AtCoderSite.login,
                formParameters = Parameters.build {
                    append("username", username)
                    append("password", password)
                    append("csrf_token", session.value.csrfToken())
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
    }
}

package com.nisecoder.gradle.atcoder

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
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.setCookie
import it.skrape.fetcher.Cookie
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.runBlocking
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class AtCoderBuildService: BuildService<AtCoderBuildService.Params> {
    interface Params: BuildServiceParameters

    fun fetchAnonymousCookie(): Cookie {
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
    fun login(session: Cookie, username: String, password: String): String {
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
                HttpStatusCode.OK, HttpStatusCode.Found -> return@runBlocking response.setCookie().first { it.name == AtCoderSite.sessionName }.value
                HttpStatusCode.Forbidden -> throw AtCoderUnauthorizedException(response.readText())
                else -> throw AtCoderException(response.status.toString())
            }
        }
    }
}

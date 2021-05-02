package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import io.github.rybalkinsd.kohttp.client.client
import io.github.rybalkinsd.kohttp.client.fork
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.url
import io.github.rybalkinsd.kohttp.interceptors.logging.HttpLoggingInterceptor
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extract
import it.skrape.fetcher.skrape
import it.skrape.fetcher.toCookie
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.net.URLDecoder
import java.nio.charset.Charset

abstract class AtCoderLoginTask : AtCoderTask() {
    @get:Input
    @set:Option(option = "username", description = "AtCoder Username")
    abstract var username: String

    @get:Input
    @set:Option(option = "password", description = "AtCoder Password")
    abstract var password: String

    @get:OutputFile
    abstract val sessionFile: RegularFileProperty

    @TaskAction
    fun login() {
        val cookies = skrape(HttpFetcher) {
            request {
                url = AtCoderSite.home
            }

            extract {
                cookies
            }
        }

        val session = cookies.find { it.name == AtCoderSite.sessionName }!!
        val csrfToken = session.value.split("%00")
            .first { it.startsWith("csrf_token") }
            .let { URLDecoder.decode(it, Charset.defaultCharset()) }
            ?.split(":")?.get(1)!!


        val result = httpPost(client {
            interceptors {
                if (logger.isDebugEnabled) {
                    +HttpLoggingInterceptor()
                }
            }
        }.fork {
            followRedirects = false
        }) {
            url(AtCoderSite.login)
            header {
                "Content-Type" to "application/x-www-form-urlencoded"
                cookie {
                    cookies.forEach {
                        it.name to it.value
                    }
                }
            }
            body {
                form {
                    addEncoded("username", username)
                    addEncoded("password", password)
                    addEncoded("csrf_token", csrfToken)
                }
            }
        }

        val loginSession = result.headers("Set-Cookie")
            .map { it.toCookie(AtCoderSite.baseUrl) }
            .firstOrNull { it.name == AtCoderSite.sessionName }
            ?: throw Exception("fail to login")

        // save cookie object which could be deserialize after end of tasks
        sessionFile.asFile.get().writeText(loginSession.value)

        logger.lifecycle("login user: $username")
    }
}


package com.nisecoder.gradle.atcoder.internal

import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.renderCookieHeader
import io.ktor.util.KtorExperimentalAPI
import org.gradle.api.file.RegularFile

fun String.csrfToken(): String {
   return split("%00")
        .first { it.startsWith("csrf_token") }
        .decodeURLQueryComponent()
        .split(":")[1]
}

@KtorExperimentalAPI
fun String.cookieValue(): String {
    return Cookie(name = AtCoderSite.sessionName, value = this, encoding = CookieEncoding.RAW)
        .let(::renderCookieHeader)
}

fun RegularFile.readFirstLine(): String {
    return asFile.readLines().first()
}

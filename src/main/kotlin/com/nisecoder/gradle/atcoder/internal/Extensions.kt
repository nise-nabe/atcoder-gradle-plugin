package com.nisecoder.gradle.atcoder.internal

import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.renderCookieHeader
import org.gradle.api.file.RegularFile

fun String.csrfToken(): String =
    split("%00")
        .first { it.startsWith("csrf_token") }
        .decodeURLQueryComponent()
        .split(":")[1]

fun String.cookieValue(): String =
    Cookie(name = AtCoderSite.SESSION_NAME, value = this, encoding = CookieEncoding.RAW)
        .let(::renderCookieHeader)

fun RegularFile.readFirstLine(): String = asFile.readLines().first()

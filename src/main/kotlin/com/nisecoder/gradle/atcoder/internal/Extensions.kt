package com.nisecoder.gradle.atcoder.internal

import io.ktor.http.decodeURLQueryComponent

fun String.csrfToken(): String {
   return split("%00")
        .first { it.startsWith("csrf_token") }
        .decodeURLQueryComponent()
        .split(":")[1]
}

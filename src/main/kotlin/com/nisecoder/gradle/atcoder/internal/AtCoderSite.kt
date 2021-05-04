package com.nisecoder.gradle.atcoder.internal

object AtCoderSite {
    const val domain = "atcoder.jp"

    const val baseUrl = "https://$domain"

    const val home = baseUrl
    const val login = "$baseUrl/login"
    const val contest = "$baseUrl/contests"

    const val sessionName = "REVEL_SESSION"
}

package com.nisecoder.gradle.atcoder.internal

@Suppress("MemberVisibilityCanBePrivate")
object AtCoderSite {
    const val DOMAIN = "atcoder.jp"

    const val BASE_URL = "https://$DOMAIN"

    const val HOME = BASE_URL
    const val LOGIN = "$BASE_URL/login"
    const val CONTEST = "$BASE_URL/contests"

    const val SESSION_NAME = "REVEL_SESSION"
}

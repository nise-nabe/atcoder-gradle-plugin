package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.internal.AtCoderSite
import it.skrape.fetcher.Cookie
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
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
}

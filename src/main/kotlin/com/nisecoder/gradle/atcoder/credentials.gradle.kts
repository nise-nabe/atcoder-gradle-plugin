package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderLoginTask
import nu.studer.gradle.credentials.domain.CredentialsContainer
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register

plugins {
    id("nu.studer.credentials")
}

tasks.register<AtCoderLoginTask>("atcoderLogin") {
    val credentials: CredentialsContainer by rootProject.extra
    username.set(credentials.getProperty("atcoder.username").toString())
    password.set(credentials.getProperty("atcoder.password").toString())
    sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
}

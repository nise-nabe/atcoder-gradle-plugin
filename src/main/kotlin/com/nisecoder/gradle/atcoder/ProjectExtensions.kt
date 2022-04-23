package com.nisecoder.gradle.atcoder

import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.services.BuildServiceSpec
import org.gradle.kotlin.dsl.credentials

fun Project.configureAtCoderService(spec: BuildServiceSpec<AtCoderBuildService.Params>) {
    spec.parameters {
        credentials.set(providers.credentials(PasswordCredentials::class, "atcoder"))
        sessionFile.set(rootProject.buildDir.resolve("atcoder/session.txt"))
        isPersistence.set(true)
    }
}

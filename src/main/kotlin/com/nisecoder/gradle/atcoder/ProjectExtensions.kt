package com.nisecoder.gradle.atcoder

import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.services.BuildServiceSpec
import org.gradle.kotlin.dsl.credentials

fun Project.configureAtCoderService(spec: BuildServiceSpec<AtCoderBuildService.Params>) {
    spec.parameters {
        sessionFile.set(rootProject.layout.buildDirectory.file("atcoder/session.txt"))
        persistence.set(true)
        credentials.set(providers.credentials(PasswordCredentials::class, "atcoder"))
    }
}

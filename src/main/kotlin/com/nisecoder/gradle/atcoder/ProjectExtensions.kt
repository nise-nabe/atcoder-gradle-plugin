package com.nisecoder.gradle.atcoder

import org.gradle.api.Project
import org.gradle.api.services.BuildServiceSpec

fun Project.configureAtCoderService(spec: BuildServiceSpec<AtCoderBuildService.Params>) {
    spec.parameters {
        sessionFile.set(rootProject.layout.buildDirectory.file("atcoder/session.txt"))
        persistence.set(true)
    }
}

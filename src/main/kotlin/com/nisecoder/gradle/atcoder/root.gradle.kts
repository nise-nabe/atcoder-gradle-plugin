package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.task.AtCoderNewContestTask

tasks {
    register<AtCoderNewContestTask>("atcoderNew") {
        outputDir.set(project.rootDir.resolve("subprojects/contests/"))
    }
}

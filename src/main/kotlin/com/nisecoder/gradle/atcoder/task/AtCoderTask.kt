package com.nisecoder.gradle.atcoder.task

import org.gradle.api.DefaultTask

abstract class AtCoderTask : DefaultTask() {
    init {
        group = "atcoder"
    }
}

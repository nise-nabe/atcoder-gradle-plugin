package com.nisecoder.gradle.atcoder.tasks

import org.gradle.api.DefaultTask

abstract class AtCoderTask: DefaultTask() {
    init {
        group = "atcoder"
    }
}

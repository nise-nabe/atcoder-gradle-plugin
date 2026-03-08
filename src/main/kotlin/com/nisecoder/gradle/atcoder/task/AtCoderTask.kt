package com.nisecoder.gradle.atcoder.task

import org.gradle.api.DefaultTask
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "temporary")
abstract class AtCoderTask : DefaultTask() {
    init {
        group = "atcoder"
    }
}

package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal

abstract class AtCoderSessionTask: AtCoderTask() {
    @get:Internal
    abstract val atcoderService: Property<AtCoderBuildService>
}

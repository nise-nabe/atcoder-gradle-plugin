package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class AtCoderLoginTask : AtCoderTask() {
    @get:Internal
    abstract val atcoderService: Property<AtCoderBuildService>

    @TaskAction
    fun login() {
        atcoderService.get().run {
            login()
            logger.lifecycle("login user: $username")
        }
    }
}

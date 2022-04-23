package com.nisecoder.gradle.atcoder.task

import com.nisecoder.gradle.atcoder.AtCoderBuildService
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class AtCoderLoginTask : AtCoderTask() {

    @get:OutputFile
    abstract val sessionFile: RegularFileProperty

    @get:Internal
    abstract val atcoderService: Property<AtCoderBuildService>

    @TaskAction
    fun login() {
        val loginSession = atcoderService.get().login()

        sessionFile.asFile.get().writeText(loginSession)

        logger.lifecycle("login user: ${atcoderService.get().username}")
    }
}

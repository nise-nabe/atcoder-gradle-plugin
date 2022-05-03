package com.nisecoder.gradle.atcoder.task

import org.gradle.api.tasks.TaskAction

abstract class AtCoderLoginTask : AtCoderSessionTask() {
    @TaskAction
    fun login() {
        atcoderService.get().run {
            login()
            logger.lifecycle("login user: $username")
        }
    }
}

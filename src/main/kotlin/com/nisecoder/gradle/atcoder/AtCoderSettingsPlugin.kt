package com.nisecoder.gradle.atcoder

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.create

class AtCoderSettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings): Unit = settings.run {
        val atcoder = extensions.create<AtCoderSettingsExtension>("atcoderSettings")
        atcoder.contestProjectBaseDir.convention(rootProject.projectDir.resolve("subprojects/contests"))

        atcoder.contestProjectBaseDir.get().let { baseDir ->
            baseDir.listFiles()
                ?.filter { it.isDirectory }
                ?.filter { it.resolve("build.gradle.kts").exists() }
                ?.map { module ->
                    include(module.name)
                    project(":${module.name}").projectDir = baseDir.resolve(module.name)
                }
        }
    }
}

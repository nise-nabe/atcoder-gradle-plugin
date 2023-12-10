package com.nisecoder.gradle.atcoder.language

import com.nisecoder.gradle.atcoder.AtCoderContestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

@Suppress("unused")
class AtCoderContestKotlinPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        project.run {
            plugins.apply(AtCoderContestPlugin::class)
            plugins.apply(KotlinPluginWrapper::class)
        }
}

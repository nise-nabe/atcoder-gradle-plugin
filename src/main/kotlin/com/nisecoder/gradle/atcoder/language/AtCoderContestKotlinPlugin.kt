package com.nisecoder.gradle.atcoder.language

import com.nisecoder.gradle.atcoder.AtCoderContestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class AtCoderContestKotlinPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        plugins.apply(AtCoderContestPlugin::class)
        plugins.apply("org.jetbrains.kotlin.jvm")

        configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
            }
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                // atcoder use 1.3.71
                languageVersion = "1.3"
                apiVersion = "1.3"

                javaParameters = true
            }
        }
    }
}

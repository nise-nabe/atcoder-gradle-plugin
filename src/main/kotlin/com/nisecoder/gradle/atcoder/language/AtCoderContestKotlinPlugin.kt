package com.nisecoder.gradle.atcoder.language

import com.nisecoder.gradle.atcoder.AtCoderContestPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AtCoderContestKotlinPlugin: Plugin<Project> {
    override fun apply(project: Project): Unit = project.run {
        plugins.apply(AtCoderContestPlugin::class)
        plugins.apply("org.jetbrains.kotlin.jvm")

        val javaToolchains = extensions.getByType<JavaToolchainService>()

        val compiler: Provider<JavaCompiler> = javaToolchains.compilerFor {
            // atcoder use openjdk 11.0.6
            languageVersion.set(JavaLanguageVersion.of(11))
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                // atcoder use 1.3.71
                languageVersion = "1.3"
                apiVersion = "1.3"

                jvmTarget = compiler.get().metadata.languageVersion.toString()
                javaParameters = true

                jdkHome = compiler.get().metadata.installationPath.asFile.absolutePath

                useIR = true
            }
        }
    }
}

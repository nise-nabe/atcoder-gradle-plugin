package com.nisecoder.gradle.atcoder

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

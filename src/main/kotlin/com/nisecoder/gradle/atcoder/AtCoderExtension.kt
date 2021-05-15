package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.internal.AtCoderLanguage
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property

interface AtCoderExtension {
    val contestName: Property<String>
    val contestTask: NamedDomainObjectContainer<AtCoderContestTaskObject>
}

abstract class AtCoderContestTaskObject {
    abstract val name: String
    abstract val language: Property<AtCoderLanguage>

    init {
        @Suppress("LeakingThis")
        language.convention(AtCoderLanguage.Kotlin)
    }
}

package com.nisecoder.gradle.atcoder

import com.nisecoder.gradle.atcoder.internal.AtCoderLanguage
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

interface AtCoderExtension {
    val contestName: Property<String>
    val contestTasks: ListProperty<String>
}

class AtCoderContestTaskObject(
    var name: String
) {
    var language: AtCoderLanguage = AtCoderLanguage.Kotlin
}

package com.nisecoder.gradle.atcoder

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class AtCoderExtension {
    abstract val contestName: Property<String>
    abstract val problems: ListProperty<String>
}

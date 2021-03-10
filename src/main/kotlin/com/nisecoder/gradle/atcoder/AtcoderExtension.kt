package com.nisecoder.gradle.atcoder

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.kotlin.dsl.listProperty

open class AtCoderExtension(objects: ObjectFactory) {
    val problems: ListProperty<String> = objects.listProperty()
}

package com.nisecoder.gradle.atcoder

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property

open class AtCoderExtension(objects: ObjectFactory) {
    val contestName: Property<String> = objects.property()
    val problems: ListProperty<String> = objects.listProperty()
}

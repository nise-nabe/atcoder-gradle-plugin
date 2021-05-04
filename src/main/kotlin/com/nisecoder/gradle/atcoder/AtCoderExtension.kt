package com.nisecoder.gradle.atcoder

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property

interface AtCoderExtension {
    val contestName: Property<String>
    val contestTask: NamedDomainObjectContainer<AtCoderContestTaskObject>
}

interface AtCoderContestTaskObject {
    val name: String
}

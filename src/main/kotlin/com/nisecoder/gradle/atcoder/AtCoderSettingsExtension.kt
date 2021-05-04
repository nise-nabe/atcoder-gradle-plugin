package com.nisecoder.gradle.atcoder

import org.gradle.api.provider.Property
import java.io.File

abstract class AtCoderSettingsExtension {
    abstract val contestProjectBaseDir: Property<File>
}

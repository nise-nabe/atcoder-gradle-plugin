package com.nisecoder.gradle

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

internal class GradleVersionProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> = Stream.of("8.14.3", "9.4.0").map { Arguments.of(it) }
}

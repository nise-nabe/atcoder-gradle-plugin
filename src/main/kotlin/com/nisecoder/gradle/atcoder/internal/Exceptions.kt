package com.nisecoder.gradle.atcoder.internal

open class AtCoderException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception()

class AtCoderUnauthorizedException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : AtCoderException()

class AtCoderNoSuchTaskException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : AtCoderException()

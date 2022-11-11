package com.webClipBoard

import java.util.*

inline fun <reified T> Optional<T>.getElseThrowNotFoundException(entityId: Long): T {
    return this.orElseThrow { NotFoundException(T::class.java.name, entityId.toString()) }
}

inline fun <reified T> Optional<T>.getElseThrowNotFoundException(entityId: Any): T {
    return this.orElseThrow { NotFoundException(T::class.java.name, entityId.toString()) }
}
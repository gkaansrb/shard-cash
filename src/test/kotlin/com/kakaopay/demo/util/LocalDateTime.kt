package com.kakaopay.demo.util

import io.mockk.clearStaticMockk
import io.mockk.every
import io.mockk.mockkStatic
import java.time.LocalDateTime

inline fun <T> withLocalDateTime(localDateTime: LocalDateTime = LocalDateTime.now(), block: () -> T): T {
    mockkStatic(LocalDateTime::class)
    every { LocalDateTime.now() } returns localDateTime
    return try {
        block()
    } finally {
        clearStaticMockk(LocalDateTime::class)
    }
}

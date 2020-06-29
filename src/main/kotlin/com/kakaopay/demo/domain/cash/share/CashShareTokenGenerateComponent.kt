package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import kotlin.random.Random
import org.springframework.stereotype.Component

@Component
class CashShareTokenGenerateComponent(private var cashShareOrderQueryDslRepository: CashShareOrderQueryDslRepository) {
    companion object {
        const val TOKEN_SIZE = 3
        val CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    }

    private suspend fun generateToken() = (1..TOKEN_SIZE)
        .map { CHAR_SET[Random.nextInt(0, CHAR_SET.size)] }
        .joinToString("")

    suspend fun create(roomId: String): String {
        var token: String
        do {
            token = generateToken()
        } while (cashShareOrderQueryDslRepository.exists(roomId = roomId, token = token))
        return token
    }
}

package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CashShareTokenGenerateComponent(private var cashShareOrderQueryDslRepository: CashShareOrderQueryDslRepository) {
    companion object {
        const val TOKEN_SIZE = 3
        val CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
    }

    private fun generateToken() = (1..TOKEN_SIZE)
        .map { CHAR_SET[Random.nextInt(0, CHAR_SET.size)] }
        .joinToString("")

    fun create(roomId: String): String {
        var token: String
        do {
            token = generateToken()
        } while (cashShareOrderQueryDslRepository.existsByValidToken(roomId = roomId, token = token))
        return token
    }
}
package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import org.springframework.stereotype.Component
import kotlin.math.pow
import kotlin.random.Random

@Component
class CashShareTokenGenerateComponent(private var cashShareOrderQueryDslRepository: CashShareOrderQueryDslRepository) {
    companion object {
        const val TOKEN_SIZE = 3
        val CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        val MAX_LOOP = CHAR_SET.size.toDouble().pow(TOKEN_SIZE.toDouble()).toLong()
    }

    private suspend fun generateToken() = (1..TOKEN_SIZE)
        .map { CHAR_SET[Random.nextInt(0, CHAR_SET.size)] }
        .joinToString("")

    suspend fun create(roomId: String): String {
        repeat((0.. MAX_LOOP).count()) {
            val token = generateToken()
            if (cashShareOrderQueryDslRepository.exists(roomId = roomId, token = token).not()) {
                return token
            }
        }

        throw IllegalStateException("최대 뿌리기 횟수[$MAX_LOOP]를 초과하였습니다. 일정 시간 이후 재 시도하시기 바랍니다.")
    }
}

package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.api.share.CashShareOrderDto
import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class CashShareService(
    private val cashShareOrderRepository: CashShareOrderRepository,
    private val cashShareOrderQueryDslRepository: CashShareOrderQueryDslRepository
) {

    companion object {
        const val TOKEN_SIZE = 3
        val CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toByteArray()
    }

    private fun generateToken() = (1..TOKEN_SIZE)
        .map { CHAR_SET[Random.nextInt(0, CHAR_SET.size)].toChar() }
        .joinToString("")

    private fun getToken(roomId: String): String {
        var token = ""
        do {
            token = generateToken()
        } while (cashShareOrderQueryDslRepository.existsByValidToken(roomId = roomId, token = token))
        return token
    }

    @Transactional(readOnly = false)
    fun create(userId: Long, roomId: String, sharePerson: Long, shareAmount: Long): String = CashShareOrder.of(
        token = getToken(roomId),
        roomId = roomId,
        sharedPerson = sharePerson,
        cash = shareAmount,
        owner = userId
    ).let { cashShareOrderRepository.save(it) }.token

    @Transactional(readOnly = false)
    fun receipt(userId: Long, roomId: String, token: String) =
        cashShareOrderQueryDslRepository.findByReceiptTarget(roomId, token)?.receipt(userId)
            ?: throw Exception("뿌리기 정보를 찾을 수 없습니다.")

    fun find(owner: Long, roomId: String, token: String) =
        cashShareOrderQueryDslRepository.findOne(owner, roomId, token)?.let { CashShareOrderDto.of(it) }
}

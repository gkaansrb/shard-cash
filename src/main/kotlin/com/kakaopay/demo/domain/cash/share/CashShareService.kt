package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.api.share.CashShareOrderDto
import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderRepository
import kotlin.random.Random
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CashShareService(private val cashShareOrderRepository: CashShareOrderRepository) {

    companion object {
        const val TOKEN_SIZE = 3
        val CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toByteArray()
    }

    private fun generateToken() = (0..TOKEN_SIZE)
        .map { CHAR_SET[Random.nextInt(0, CHAR_SET.size)] }
        .joinToString("")

    private fun getToken(): String {
        var token = ""
        do {
            token = generateToken()
        } while (cashShareOrderRepository.existsLookUpTargetToken(token).not())
        return token
    }

    @Transactional(readOnly = false)
    fun create(userId: String, roomId: String, sharePerson: Long, shareAmount: Long): String = CashShareOrder.of(
        token = getToken(),
        roomId = roomId,
        sharedPerson = sharePerson,
        cash = shareAmount,
        owner = userId
    ).let { cashShareOrderRepository.save(it) }.token

    @Transactional(readOnly = false)
    fun receipt(userId: String, roomId: String, token: String) =
        cashShareOrderRepository.findOne(roomId, token).receipt(userId)

    fun find(owner: String, roomId: String, token: String) =
        cashShareOrderRepository.findOne(owner, roomId, token).let { CashShareOrderDto.of(it) }
}

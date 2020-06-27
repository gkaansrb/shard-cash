package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.api.share.CashShareOrderDto
import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderRepository
import com.kakaopay.demo.domain.common.DataNotFoundException
import com.kakaopay.demo.domain.common.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CashShareService(
    private val cashShareOrderRepository: CashShareOrderRepository,
    private val cashShareOrderQueryDslRepository: CashShareOrderQueryDslRepository
) {
    @Transactional(readOnly = false)
    fun create(token: String, userId: Long, roomId: String, sharePerson: Long, shareAmount: Long): String = CashShareOrder.of(
        token = token,
        roomId = roomId,
        sharedPerson = sharePerson,
        shareRequestAmount = shareAmount,
        owner = userId
    ).let { cashShareOrderRepository.save(it) }.token

    @Transactional(readOnly = false)
    fun share(userId: Long, roomId: String, token: String) =
        cashShareOrderQueryDslRepository.findSharableOrder(roomId, token)?.receiptShare(userId)
            ?: throw DataNotFoundException(ErrorCode.DATA_NOT_FOUND.description)

    fun find(owner: Long, roomId: String, token: String) =
        cashShareOrderQueryDslRepository.findOne(owner, roomId, token)?.let { CashShareOrderDto.of(it) }
            ?: throw DataNotFoundException(ErrorCode.DATA_NOT_FOUND.description)
}

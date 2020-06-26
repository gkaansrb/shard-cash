package com.kakaopay.demo.domain.cash.share.store

import java.time.LocalDateTime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface CashShareOrderRepository : JpaRepository<CashShareOrder, Long>, QuerydslPredicateExecutor<CashShareOrder> {

    companion object {
        val qCashShareOrder = QCashShareOrder.cashShareOrder
    }

    fun existsLookUpTargetToken(token: String): Boolean =
        exists(
            qCashShareOrder.token.eq(token).and(qCashShareOrder.lookUpDeadLine.goe(LocalDateTime.now()))
        )

    fun findOne(roomId: String, token: String): CashShareOrder =
        findOne(
            qCashShareOrder.token.eq(token)
                .and(qCashShareOrder.lookUpDeadLine.goe(LocalDateTime.now()))
                .and(qCashShareOrder.roomId.eq(roomId))
        ).orElseThrow { Exception("뿌리기 정보를 찾을 수 없습니") }

    fun findOne(owner: String, roomId: String, token: String): CashShareOrder =
        findOne(
            qCashShareOrder.token.eq(token)
                .and(qCashShareOrder.lookUpDeadLine.goe(LocalDateTime.now()))
                .and(qCashShareOrder.owner.eq(owner))
                .and(qCashShareOrder.roomId.eq(roomId))
        ).orElseThrow { Exception("뿌리기 정보를 찾을 수 없습니다") }
}

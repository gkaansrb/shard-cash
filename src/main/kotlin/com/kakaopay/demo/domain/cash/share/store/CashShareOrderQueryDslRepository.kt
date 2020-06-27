package com.kakaopay.demo.domain.cash.share.store

import java.time.LocalDateTime
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class CashShareOrderQueryDslRepository : QuerydslRepositorySupport(QCashShareOrder::class.java) {

    companion object {
        val qCashShareOrder: QCashShareOrder = QCashShareOrder.cashShareOrder
    }

    fun exists(roomId: String, token: String): Boolean =
        from(qCashShareOrder)
            .where(
                qCashShareOrder.token.eq(token)
                    .and(qCashShareOrder.roomId.eq(roomId))
                    .and(qCashShareOrder.sharedDeadLine.goe(LocalDateTime.now()))
            )
            .orderBy(qCashShareOrder.sharedDeadLine.desc())
            .fetchFirst() != null

    fun findSharableOrder(roomId: String, token: String): CashShareOrder? =
        from(qCashShareOrder)
            .where(
                qCashShareOrder.token.eq(token)
                    .and(qCashShareOrder.roomId.eq(roomId))
            )
            .orderBy(qCashShareOrder.sharedDeadLine.desc())
            .fetchFirst()

    fun findOne(owner: Long, roomId: String, token: String): CashShareOrder? =
        from(qCashShareOrder)
            .where(
                qCashShareOrder.token.eq(token)
                    .and(qCashShareOrder.owner.eq(owner))
                    .and(qCashShareOrder.roomId.eq(roomId))
                    .and(qCashShareOrder.lookUpDeadLine.goe(LocalDateTime.now()))
            )
            .orderBy(qCashShareOrder.lookUpDeadLine.asc())
            .fetchFirst()
}

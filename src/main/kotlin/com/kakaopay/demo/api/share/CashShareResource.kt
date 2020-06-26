package com.kakaopay.demo.api.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashSharedUser
import java.time.LocalDateTime

data class CashShareRequest(
    val shareAmount: Long,
    val sharePerson: Long
)

data class CashShareOrderDto(
    val cash: Long,
    val sharedAt: LocalDateTime,
    val sharedAmount: Long,
    val sharedUsers: List<CashSharedUserDto>
) {
    companion object {
        fun of(order: CashShareOrder) = CashShareOrderDto(
            cash = order.cash,
            sharedAt = order.createdAt,
            sharedAmount = order.sharedAmount,
            sharedUsers = order.cashSharedUsers.map { CashSharedUserDto.of(it) }
        )
    }
}

data class CashSharedUserDto(val userId: String, val cash: Long) {
    companion object {
        fun of(cashSharedUser: CashSharedUser) =
            CashSharedUserDto(userId = cashSharedUser.userId, cash = cashSharedUser.cash)
    }
}

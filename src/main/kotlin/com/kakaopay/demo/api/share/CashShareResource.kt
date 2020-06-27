package com.kakaopay.demo.api.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashSharedUser
import java.time.LocalDateTime
import javax.validation.constraints.Min

data class CashShareRequest(
    @get:Min(1, message = "뿌리는 금액은 1원 이상 이어야 합니다")
    val shareAmount: Long,
    @get:Min(1, message = "뿌리는 인원은 1명 이상 이어야 합니다")
    val sharePerson: Long
)

data class CashShareOrderDto(
    val shareRequestAmount: Long,
    val sharedAt: LocalDateTime,
    val sharedAmount: Long,
    val sharedUsers: List<CashSharedUserDto>
) {
    companion object {
        fun of(order: CashShareOrder) = CashShareOrderDto(
            shareRequestAmount = order.shareRequestAmount,
            sharedAt = order.createdAt,
            sharedAmount = order.sharedAmount,
            sharedUsers = order.cashSharedUsers.map { CashSharedUserDto.of(it) }
        )
    }
}

data class CashSharedUserDto(val userId: Long, val cash: Long) {
    companion object {
        fun of(cashSharedUser: CashSharedUser) =
            CashSharedUserDto(userId = cashSharedUser.userId, cash = cashSharedUser.sharedAmount)
    }
}

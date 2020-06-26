package com.kakaopay.demo.domain.cash.share.store

import com.kakaopay.demo.domain.common.AuditingEntity
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "cash_share_order")
data class CashShareOrder(
    @Column(name = "token")
    val token: String,
    @Column(name = "roomId")
    val roomId: String,
    @Column(name = "owner")
    val owner: String,
    @Column(name = "cash")
    val cash: Long,
    @Column(name = "shared_person")
    val sharedPerson: Long,
    @Column(name = "shared_dead_line")
    val sharedDeadLine: LocalDateTime,
    @Column(name = "look_up_dead_line")
    val lookUpDeadLine: LocalDateTime,
    @Column(name = "shared_amount")
    var sharedAmount: Long = 0,
    @OneToMany(mappedBy = "cashShareOrder", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val cashShareds: List<CashShared>
) : AuditingEntity() {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @OneToMany(mappedBy = "cashShareOrder", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val cashSharedUsers: MutableList<CashSharedUser> = mutableListOf()

    fun receipt(userId: String): Long {
        valid(userId)

        return cashShareds.first { it.status == CashShared.Status.READY }
            .apply { shared() }
            .let { CashSharedUser(userId, it.id!!, it.sharedAmount, this) }
            .apply { cashSharedUsers.add(this) }
            .cash
    }

    private fun valid(userId: String) {
        check(cash > sharedAmount) { "더 이상 남은 잔액이 없습니다." }
        check(owner != userId) { "획득 대상자가 아닙니다." }
        check(cashSharedUsers.none { it.userId == userId }) { "이미 처리된 유저입니다." }
    }

    companion object {
        fun of(token: String, roomId: String, owner: String, cash: Long, sharedPerson: Long): CashShareOrder {
            val now = LocalDateTime.now()
            return CashShareOrder(
                token = token,
                roomId = roomId,
                owner = owner,
                cash = cash,
                sharedPerson = sharedPerson,
                sharedDeadLine = now.plusMinutes(10),
                lookUpDeadLine = now.plusDays(7),
                cashShareds = listOf()
            )
        }
    }
}

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
import javax.persistence.Index
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Version
import kotlin.math.floor

@Entity
@Table(name = "cash_share_order",
    indexes = [Index(name = "cash_share_order_idx01", columnList = "roomId, token")]
)
data class CashShareOrder(
    @Column(name = "token")
    val token: String,
    @Column(name = "roomId")
    val roomId: String,
    @Column(name = "owner")
    val owner: Long,
    @Column(name = "cash")
    val cash: Long,
    @Column(name = "shared_person")
    val sharedPerson: Long,
    @Column(name = "shared_dead_line")
    val sharedDeadLine: LocalDateTime,
    @Column(name = "look_up_dead_line")
    val lookUpDeadLine: LocalDateTime,
    @Column(name = "shared_amount")
    var sharedAmount: Long = 0
) : AuditingEntity() {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Version
    @Column(name = "version")
    var version: Long = 0

    @OneToMany(mappedBy = "cashShareOrder", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val cashSharedUsers: MutableList<CashSharedUser> = mutableListOf()

    @OneToMany(mappedBy = "cashShareOrder", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val cashShareds: MutableList<CashShared> = mutableListOf()

    fun receipt(userId: Long): Long {
        valid(userId)
        return cashShareds.first { it.status == CashShared.Status.READY }
            .apply { this.shared() }
            .let { CashSharedUser(userId = userId, cash = it.sharedAmount, cashSharedId = it.id!!, cashShareOrder = this) }
            .apply { cashSharedUsers.add(this) }
            .apply { sharedAmount += this.cash }
            .cash
    }

    private fun valid(userId: Long) {
        check(cash > sharedAmount) { "더 이상 남은 잔액이 없습니다." }
        check(owner != userId) { "획득 대상자가 아닙니다." }
        check(sharedDeadLine.isBefore(LocalDateTime.now()).not()) { "획득 시간이 초과하였습니다." }
        check(cashSharedUsers.none { it.userId == userId }) { "이미 처리된 유저입니다." }
    }

    fun shared() {
        val baseShare = floor(cash / sharedPerson.toDouble()).toLong()
        (1.. sharedPerson).fold(cash - (baseShare * sharedPerson)) {
            remainAmount, it ->
            if (remainAmount > 0) {
                cashShareds.add(CashShared(baseShare + 1, this))
                remainAmount - 1
            } else {
                cashShareds.add(CashShared(baseShare, this))
                0
            }
        }
     }

    companion object {
        fun of(token: String, roomId: String, owner: Long, cash: Long, sharedPerson: Long): CashShareOrder {
            val now = LocalDateTime.now()
            return CashShareOrder(
                token = token,
                roomId = roomId,
                owner = owner,
                cash = cash,
                sharedPerson = sharedPerson,
                sharedDeadLine = now.plusMinutes(10),
                lookUpDeadLine = now.plusDays(7)
            ).apply { shared() }
        }
    }
}

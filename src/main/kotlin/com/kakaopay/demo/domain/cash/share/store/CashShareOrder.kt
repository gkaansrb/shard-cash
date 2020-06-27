package com.kakaopay.demo.domain.cash.share.store

import com.kakaopay.demo.domain.common.AuditingEntity
import com.kakaopay.demo.domain.common.ErrorCode
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
@Table(
    name = "cash_share_order",
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
        receiptValid(userId)
        return cashShareds.first { it.status == CashShared.Status.READY }
            .apply { this.shared() }
            .let {
                CashSharedUser(
                    userId = userId,
                    cash = it.sharedAmount,
                    cashSharedId = it.id!!,
                    cashShareOrder = this
                )
            }
            .apply { cashSharedUsers.add(this) }
            .apply { sharedAmount += this.cash }
            .cash
    }

    private fun receiptValid(userId: Long) {
        check(cash > sharedAmount) { ErrorCode.SHARED_COMPLETED.description }
        check(owner != userId) { ErrorCode.SHARED_TARGET.description }
        check(
            sharedDeadLine.isBefore(LocalDateTime.now()).not()
        ) { ErrorCode.SHARED_TIME_OUT.description }
        check(cashSharedUsers.none { it.userId == userId }) { ErrorCode.SHARED_USER.description }
    }

    fun shared() {
        val baseShare = floor(cash / sharedPerson.toDouble()).toLong()
        (1..sharedPerson).fold(cash - (baseShare * sharedPerson)) { remainAmount, it ->
            if (remainAmount > 0) {
                cashShareds.add(CashShared(baseShare + 1, this))
                remainAmount - 1
            } else {
                cashShareds.add(CashShared(baseShare, this))
                0
            }
        }
    }

    private fun valid() {
        check(cash > 0) { ErrorCode.SHARE_AMOUNT_EMPTY.description }
        check(sharedPerson > 0) { ErrorCode.SHARE_PERSON_EMPTY.description }
    }

    private fun sharedValid() {
        check(cash == cashShareds.map { it.sharedAmount }.sum()) {
            "${ErrorCode.SHARE_FAILED.description} - $cashShareds"
        }
    }

    companion object {
        fun of(
            token: String,
            roomId: String,
            owner: Long,
            cash: Long,
            sharedPerson: Long
        ): CashShareOrder {
            val now = LocalDateTime.now()
            return CashShareOrder(
                token = token,
                roomId = roomId,
                owner = owner,
                cash = cash,
                sharedPerson = sharedPerson,
                sharedDeadLine = now.plusMinutes(10),
                lookUpDeadLine = now.plusDays(7)
            )
                .apply { valid() }
                .apply { shared() }
                .apply { sharedValid() }
        }
    }
}

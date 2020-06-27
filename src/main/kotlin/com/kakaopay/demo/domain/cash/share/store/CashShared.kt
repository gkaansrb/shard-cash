package com.kakaopay.demo.domain.cash.share.store

import com.kakaopay.demo.domain.common.AuditingEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "cash_shared",
    indexes = [Index(name = "cash_shared_idx01", columnList = "cash_share_order_id")]
)
data class CashShared(
    @Column(name = "shared_amount")
    val sharedAmount: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_share_order_id")
    val cashShareOrder: CashShareOrder
) : AuditingEntity() {
    fun shared() {
        status = Status.SHARED
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: Status = Status.READY

    enum class Status {
        READY, SHARED
    }
}

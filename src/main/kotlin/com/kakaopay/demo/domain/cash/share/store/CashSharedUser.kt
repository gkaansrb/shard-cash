package com.kakaopay.demo.domain.cash.share.store

import com.kakaopay.demo.domain.common.AuditingEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(
    name = "cash_shared_user",
    indexes = [javax.persistence.Index(name = "cash_shared_user_idx01", columnList = "cash_share_order_id")]
)
data class CashSharedUser(
    @Column(name = "user_id")
    val userId: Long,
    @Column(name = "cash")
    val cash: Long,
    @Column(name = "cash_shared_id")
    val cashSharedId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn("cash_share_order_id")
    val cashShareOrder: CashShareOrder
) : AuditingEntity() {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set
}

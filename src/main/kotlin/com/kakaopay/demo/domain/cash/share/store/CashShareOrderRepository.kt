package com.kakaopay.demo.domain.cash.share.store

import org.springframework.data.jpa.repository.JpaRepository

interface CashShareOrderRepository : JpaRepository<CashShareOrder, Long>

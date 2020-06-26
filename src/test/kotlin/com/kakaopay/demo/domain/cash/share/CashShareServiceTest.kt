package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class CashShareServiceTest {

    private val queryRepository = mock(CashShareOrderQueryDslRepository::class.java)
    private val repository = mock(CashShareOrderRepository::class.java)

    private val cashShareService: CashShareService = CashShareService(
        cashShareOrderQueryDslRepository = queryRepository,
        cashShareOrderRepository = repository
    )

    @Test
    fun create() {
        cashShareService.create(userId = 1111, roomId = "roomId", shareAmount = 1000, sharePerson = 5)
            .apply { assert(length == 3) }
    }

    @Test
    fun receipt() {
    }

    @Test
    fun find() {
    }
}
package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrder
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.kakaopay.demo.domain.cash.share.store.CashShareOrderRepository
import com.kakaopay.demo.util.invokeData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import kotlin.random.Random

@RunWith(MockitoJUnitRunner::class)
internal class CashShareServiceTest {

    private val queryRepository = mock(CashShareOrderQueryDslRepository::class.java)
    private val repository = mock(CashShareOrderRepository::class.java)

    private val cashShareService: CashShareService = CashShareService(
        cashShareOrderQueryDslRepository = queryRepository,
        cashShareOrderRepository = repository
    )

    @Test
    fun `신규 생성된 뿌리기 토큰은 생성 테스트`() {
        var cashShareOrder: CashShareOrder? = null
        val shareAmount: Long = 1000
        val sharePerson: Long = 5
        val roomId: String = "roomId"
        val owner: Long = 1111
        Mockito.`when`(repository.save(Mockito.any(CashShareOrder::class.java))).thenAnswer {
            cashShareOrder = it.arguments[0] as CashShareOrder
            cashShareOrder
        }
        cashShareService.create(
            userId = owner,
            roomId = roomId,
            shareAmount = shareAmount,
            sharePerson = sharePerson
        ).apply { assert(length == 3) }

        cashShareOrder?.apply {
            assert(cashShareds.size == 5)
            assert(cashSharedUsers.size == 0)
            assert(cash == shareAmount)
            assert(sharedPerson == sharePerson)
            cashShareds.forEach {
                assert(it.sharedAmount == 200L)
            }
        }
    }

    @Test
    fun `100원을 1명에게 뿌리면 100원을 받을 수 있다`() {
        val roomId = "roomID"
        val shareUserId = 1234L
        var cashShareOrder: CashShareOrder? = null
        Mockito.`when`(repository.save(Mockito.any(CashShareOrder::class.java))).thenAnswer {
            cashShareOrder = it.arguments[0] as CashShareOrder
            cashShareOrder!!.cashShareds.forEach { cashShared ->
                invokeData(cashShared, "id", Random.nextLong())
            }
            cashShareOrder
        }

        cashShareService.create(userId = 1111, roomId = roomId, shareAmount = 100, sharePerson = 1)
        cashShareOrder?.apply {
            Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token)).thenReturn(this)
            val receipt = cashShareService.receipt(shareUserId, roomId, token)
            assert(receipt == 100L)
        }
    }


    @Test
    fun `이미 받은 유저는 또 받을 수 없다`() {
        val token = "toc"
        val roomId = "roomID"
        val owner = 111L
        val shareUserId = 1234L
        val cashShareOrder = CashShareOrder.of(token, roomId, owner, 100, sharedPerson = 2)
        cashShareOrder.cashShareds.forEach {
            invokeData(it, "id", Random.nextLong())
        }

        Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token))
            .thenReturn(cashShareOrder)

        cashShareService.receipt(shareUserId, roomId, token).apply { assert(this == 50L) }

        assertThrows<Exception> { cashShareService.receipt(shareUserId, roomId, token) }
            .apply { assert(message == "이미 처리된 유저입니다.") }
    }

    @Test
    fun `서로 다른 유저는 각각 돈을 받을 수 있다`() {
        val token = "toc"
        val roomId = "roomID"
        val owner = 111L
        val shareUserId = 1234L
        val shareUserId2 = 4321L
        val cashShareOrder = CashShareOrder.of(token, roomId, owner, 100, sharedPerson = 2)
        cashShareOrder.cashShareds.forEach {
            invokeData(it, "id", Random.nextLong())
        }

        Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token))
            .thenReturn(cashShareOrder)

        cashShareService.receipt(shareUserId, roomId, token).apply { assert(this == 50L) }
        cashShareService.receipt(shareUserId2, roomId, token).apply { assert(this == 50L) }
    }

    @Test
    fun `뿌리기 시간이 지난 토큰은 돈을 받을 수 없다`() {
        val token = "toc"
        val roomId = "roomID"
        val owner = 111L
        val shareUserId = 1234L
        val cashShareOrder = CashShareOrder.of(token, roomId, owner, 100, sharedPerson = 2)
        invokeData(cashShareOrder, "sharedDeadLine", LocalDateTime.now().minusMinutes(20))
        cashShareOrder.cashShareds.forEach {
            invokeData(it, "id", Random.nextLong())
        }

        Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token))
            .thenReturn(cashShareOrder)

        assertThrows<Exception> { cashShareService.receipt(shareUserId, roomId, token) }
            .apply {
                assert(message == "획득 시간이 초과하였습니다.")
            }
    }

    @Test
    fun `뿌리기를 실행한 owner 는 돈을 받을 수 없다`() {
        val token = "toc"
        val roomId = "roomID"
        val owner = 111L
        val cashShareOrder = CashShareOrder.of(token, roomId, owner, 100, sharedPerson = 2)
        cashShareOrder.cashShareds.forEach {
            invokeData(it, "id", Random.nextLong())
        }

        Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token))
            .thenReturn(cashShareOrder)

        assertThrows<Exception> { cashShareService.receipt(owner, roomId, token) }
            .apply { assert(message == "획득 대상자가 아닙니다") }
    }

    @Test
    fun `뿌리기가 얼마나 진행되었는지 볼 수 있다`() {
        val token = "toc"
        val roomId = "roomID"
        val owner = 111L
        val shareUserId = 1234L
        val cashShareOrder = CashShareOrder.of(token, roomId, owner, 100, sharedPerson = 2)
        cashShareOrder.cashShareds.forEach { invokeData(it, "id", Random.nextLong()) }
        Mockito.`when`(queryRepository.findByReceiptTarget(roomId, token)).thenReturn(cashShareOrder)
        Mockito.`when`(queryRepository.findOne(owner, roomId, token)).thenReturn(cashShareOrder)
        cashShareService.receipt(shareUserId, roomId, token).apply { assert(this == 50L) }

        cashShareService.find(shareUserId, roomId, token)
            ?.apply {
                assert(cash == 100L)
                assert(sharedAt == cashShareOrder.createdAt)
                assert(sharedAmount == 50L)
                assert(sharedUsers.size == 1)
                sharedUsers.first().apply {
                    assert(userId == shareUserId)
                    assert(cash == 50L)
                }
            } ?: throw Exception("error")
    }
}
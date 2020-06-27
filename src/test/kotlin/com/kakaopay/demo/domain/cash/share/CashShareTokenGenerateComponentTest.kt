package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.nhaarman.mockito_kotlin.any
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CashShareTokenGenerateComponentTest {

    private val queryRepository = Mockito.mock(CashShareOrderQueryDslRepository::class.java)

    private val duplicateTest = Regex("""\d+""")

    private var component: CashShareTokenGenerateComponent = CashShareTokenGenerateComponent(queryRepository)

    @Test
    fun `숫자가 있으면 token 이 중복된다고 판단하고 문자열로만 구성된 token 을 만들어 낸다(중복 토큰 방지 테스트)`() {
        Mockito.`when`(queryRepository.exists(any(), any())).thenAnswer {
            val arguments = it.arguments
            val token = (arguments[1] as String)
            duplicateTest.containsMatchIn(token)
        }

        assert(duplicateTest.containsMatchIn(component.create("roomId")).not())
    }
}

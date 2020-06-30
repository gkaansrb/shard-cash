package com.kakaopay.demo.domain.cash.share

import com.kakaopay.demo.domain.cash.share.store.CashShareOrderQueryDslRepository
import com.nhaarman.mockito_kotlin.any
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
        runBlocking {
            Mockito.`when`(queryRepository.exists(any(), any())).thenAnswer {
                val arguments = it.arguments
                val token = (arguments[1] as String)
                duplicateTest.containsMatchIn(token)
            }

            assert(duplicateTest.containsMatchIn(component.create("roomId")).not())
        }
    }

    @Test
    fun `최대 토큰 발급갯수 초과하면 에러를 뱉는다`() {
        runBlocking {
            Mockito.`when`(queryRepository.exists(any(), any())).thenAnswer {
                true
            }
        }

        assertThrows<IllegalStateException> {
            runBlocking {
                component.create("roomId")
            }
        }.apply {
            assert(message == "최대 뿌리기 횟수[${CashShareTokenGenerateComponent.MAX_LOOP}]를 초과하였습니다. 일정 시간 이후 재 시도하시기 바랍니다.")
        }
    }
}

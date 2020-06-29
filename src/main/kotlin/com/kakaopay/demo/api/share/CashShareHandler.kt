package com.kakaopay.demo.api.share

import com.kakaopay.demo.domain.cash.share.CashShareService
import com.kakaopay.demo.domain.cash.share.CashShareTokenGenerateComponent
import com.kakaopay.demo.domain.common.DataNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.json

@Component
class CashShareHandler(
    private val service: CashShareService,
    private val tokenGenerator: CashShareTokenGenerateComponent
) {
    suspend fun hello(request: ServerRequest) = ServerResponse.ok().json().bodyValueAndAwait("hi")

    suspend fun create(request: ServerRequest) =
        request.cashShareRequest().let { (shareAmount, sharePerson) ->
            service.create(
                token = tokenGenerator.create(request.getRoomId()),
                userId = request.getUserId(),
                roomId = request.getRoomId(),
                sharePerson = sharePerson,
                shareAmount = shareAmount
            ).let { ServerResponse.ok().json().bodyValueAndAwait(it) }
        }

    suspend fun receipt(request: ServerRequest) =
        request.let {
            service.share(
                userId = it.getUserId(),
                roomId = it.getRoomId(),
                token = it.getToken()
            ).let { ServerResponse.ok().json().bodyValueAndAwait(it) }
        }

    suspend fun find(request: ServerRequest) =
        request.let {
            service.find(
                owner = it.getUserId(),
                roomId = it.getRoomId(),
                token = it.getToken()
            ).let { ServerResponse.ok().json().bodyValueAndAwait(it) }
        }

    private suspend fun ServerRequest.getToken() = pathVariable("token")

    private suspend fun ServerRequest.getUserId() =
        headers().header("X-USER-ID").firstOrNull()?.toLong() ?: throw DataNotFoundException("사용자 정보를 찾을 수 없습니다")

    private suspend fun ServerRequest.getRoomId() =
        headers().header("X-ROOM-ID").firstOrNull() ?: throw DataNotFoundException("채팅방 정보를 찾을 수 없습니다")

    private suspend fun ServerRequest.cashShareRequest() = awaitBody<CashShareRequest>()
}

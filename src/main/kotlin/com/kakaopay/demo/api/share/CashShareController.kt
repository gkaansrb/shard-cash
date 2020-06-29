package com.kakaopay.demo.api.share

import com.kakaopay.demo.domain.cash.share.CashShareService
import com.kakaopay.demo.domain.cash.share.CashShareTokenGenerateComponent
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cash/share")
class CashShareController(
    private val cashShareService: CashShareService,
    private var cashShareTokenGenerateComponent: CashShareTokenGenerateComponent
) {
    //
    // @PostMapping("")
    // fun create(
    //     @RequestHeader("X-USER-ID") userId: Long,
    //     @RequestHeader("X-ROOM-ID") roomId: String,
    //     @RequestBody(required = true) cashShareRequest: CashShareRequest
    // ) = cashShareService.create(
    //     token = cashShareTokenGenerateComponent.create(roomId),
    //     userId = userId,
    //     roomId = roomId,
    //     sharePerson = cashShareRequest.sharePerson,
    //     shareAmount = cashShareRequest.shareAmount
    // )
    //
    // @PutMapping("/{token}")
    // fun receipt(
    //     @RequestHeader("X-USER-ID") userId: Long,
    //     @RequestHeader("X-ROOM-ID") roomId: String,
    //     @PathVariable("token") token: String
    // ) = cashShareService.share(userId = userId, roomId = roomId, token = token)
    //
    // @GetMapping("/{token}")
    // fun find(
    //     @RequestHeader("X-USER-ID") userId: Long,
    //     @RequestHeader("X-ROOM-ID") roomId: String,
    //     @PathVariable("token") token: String
    // ) = cashShareService.find(owner = userId, roomId = roomId, token = token)
}

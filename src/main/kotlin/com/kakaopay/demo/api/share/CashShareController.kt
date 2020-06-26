package com.kakaopay.demo.api.share

import com.kakaopay.demo.domain.cash.share.CashShareService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cash/share")
class CashShareController(private val cashShareService: CashShareService) {

    @PostMapping("")
    fun create(@RequestBody(required = true) cashShareRequest: CashShareRequest): String {
        return cashShareService.create("userId", "roomId", cashShareRequest.sharePerson, cashShareRequest.shareAmount)
    }

    @PutMapping("/{token}")
    fun receipt(@PathVariable("token") token: String): Long {
        return cashShareService.receipt("userId", "roomId", token)
    }

    @GetMapping("/{token}")
    fun find(@PathVariable("token") token: String): CashShareOrderDto {
        return cashShareService.find("userId", "roomId", token)
    }
}

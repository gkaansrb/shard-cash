package com.kakaopay.demo.api

import com.kakaopay.demo.domain.HelloService
import com.sun.org.apache.xml.internal.security.utils.HelperNodeList
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
@RequestMapping("/hello")
class HelloController(private val helloService: HelloService) {

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long): String {
        return helloService.get(id)?.toString() ?: "메시지를 찾을 수 없습니다."
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody(required = true) message: String): String {
        return helloService.update(id, message)?.toString() ?: "업뎃 실패"
    }

    @PostMapping("")
    fun create(@RequestBody(required = true) message: String): String {
        return helloService.created(message).toString()
    }

    @DeleteMapping("/{id}")
    fun deleted(@PathVariable("id") id: Long) {
        helloService.deleted(id)
    }
}
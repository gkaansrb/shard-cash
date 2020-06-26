package com.kakaopay.demo.domain

import org.springframework.stereotype.Service

@Service
class HelloService(private val helloRepository: HelloRepository) {

    fun get(id: Long): Hello? = helloRepository.findById(id).get()

    fun deleted(id: Long) {
        get(id)?.apply { helloRepository.delete(this) }
    }

    fun update(id: Long, message: String): Hello? = get(id)?.apply { update(message = message) }

    fun created(message: String) = helloRepository.save(Hello(message))
}

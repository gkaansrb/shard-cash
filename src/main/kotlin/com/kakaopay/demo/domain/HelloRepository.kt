package com.kakaopay.demo.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.stereotype.Repository

@Repository
interface HelloRepository : JpaRepository<Hello, Long>, QuerydslPredicateExecutor<Hello> {
    companion object {
        private val qHello = QHello.hello
    }
}
package com.kakaopay.demo.domain.common

enum class ErrorCode(val description: String) {
    DATA_NOT_FOUND(description = "뿌리기 정보가 유효하지 않거나 찾을 수 없습니다"),
    SHARED_COMPLETED(description = "더 이상 남은 잔액이 없습니다"),
    SHARED_TARGET(description = "획득 대상이 아닙니다"),
    SHARED_TIME_OUT(description = "획득 가능한 시간이 초과하였습니다"),
    SHARED_USER(description = "이미 획득하였습니다"),
}
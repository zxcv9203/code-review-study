package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class DuplicatedException(
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    message: String,
    private val duplicateValue: Any? = null,
) : BusinessException(status, message) {

    override fun printLog() {
        log.warn("$message : 중복 값: $duplicateValue")
    }
}
package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class DuplicatedException(
    message: String,
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    private val duplicateValue: Any? = null,
) : BusinessException(status, message) {
    override val errorMessage: String
        get() = "$message 중복 값: $duplicateValue"
}
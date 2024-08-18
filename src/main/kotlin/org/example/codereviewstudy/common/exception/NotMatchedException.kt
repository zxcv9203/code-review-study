package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class NotMatchedException(
    status: HttpStatus = HttpStatus.BAD_REQUEST,
    message: String,
    private val expectedValue: Any? = null,
    private val actualValue: Any? = null,
) : BusinessException(status, message) {

    override val errorMessage: String
        get() = "$message : 예상 값: $expectedValue, 실제 값: $actualValue"

}
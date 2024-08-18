package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class NotFoundException(
    status: HttpStatus = HttpStatus.NOT_FOUND,
    message: String,
    private val notFoundValue: Any? = null,
) : BusinessException(status, message) {

    override val errorMessage: String
        get() = "$message 해당 값을 찾을 수 없습니다.: $notFoundValue"

}
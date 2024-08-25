package org.example.codereviewstudy.common.exception

import org.springframework.http.HttpStatus

abstract class BusinessException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException() {
    abstract val errorMessage: String
}
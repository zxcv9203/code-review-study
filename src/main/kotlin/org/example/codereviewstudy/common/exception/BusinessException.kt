package org.example.codereviewstudy.common.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

abstract class BusinessException(
    val status: HttpStatus,
    override val message: String,
    protected val log: Logger = LoggerFactory.getLogger(BusinessException::class.java)
) : RuntimeException() {
    abstract fun printLog()
}
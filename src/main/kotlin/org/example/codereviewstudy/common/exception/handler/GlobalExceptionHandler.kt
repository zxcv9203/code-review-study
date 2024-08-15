package org.example.codereviewstudy.common.exception.handler

import org.example.codereviewstudy.common.exception.BusinessException
import org.example.codereviewstudy.common.model.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
) {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        e.printLog()

        val response = ApiResponse.error(
            e.status.value().toString(),
            e.message
        )

        return ResponseEntity.status(e.status)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("서버 에러 : ", e)

        val response = ApiResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
            "서버 에러가 발생했습니다."
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
}
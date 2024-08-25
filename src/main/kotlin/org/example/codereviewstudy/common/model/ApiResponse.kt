package org.example.codereviewstudy.common.model

import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T,
) {
    companion object {
        fun <T> success(status: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(status.value().toString(), message, data)
        }
        fun success(status: HttpStatus, message: String): ApiResponse<Unit> {
            return ApiResponse(status.value().toString(), message, Unit)
        }

        fun error(status: HttpStatus, message: String): ApiResponse<Unit> {
            return ApiResponse(status.value().toString(), message, Unit)
        }
        fun <T>error(status: HttpStatus, message: String, data: T): ApiResponse<T> {
            return ApiResponse(status.value().toString(), message, data)
        }
    }
}
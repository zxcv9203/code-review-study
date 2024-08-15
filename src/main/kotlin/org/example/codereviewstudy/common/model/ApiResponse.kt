package org.example.codereviewstudy.common.model

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T,
) {
    companion object {
        fun <T> success(status: String, message: String, data: T): ApiResponse<T> {
            return ApiResponse(status, message, data)
        }
        fun success(status: String, message: String): ApiResponse<Unit> {
            return ApiResponse(status, message, Unit)
        }

        fun error(status: String, message: String): ApiResponse<Unit> {
            return ApiResponse(status, message, Unit)
        }
    }
}
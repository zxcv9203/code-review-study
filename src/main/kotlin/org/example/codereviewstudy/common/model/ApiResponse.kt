package org.example.codereviewstudy.common.model

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null,
) {
    companion object {
        fun <T> success(status: String, message: String, data: T): ApiResponse<T> {
            return ApiResponse(status, message, data)
        }

        fun <T> error(status: String, message: String): ApiResponse<T> {
            return ApiResponse(status, message)
        }
    }
}
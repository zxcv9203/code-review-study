package org.example.codereviewstudy.common.exception.model

data class ValidationError(
    val field: String,
    val message: String,
)
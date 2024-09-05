package org.example.codereviewstudy.common.model

data class Page<T>(
    val content: List<T>,
    val isLast: Boolean
)
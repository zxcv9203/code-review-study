package org.example.codereviewstudy.common.model.page

data class Page<T>(
    val content: List<T>,
    val isLast: Boolean
)
package org.example.codereviewstudy.common.model.page

import org.example.codereviewstudy.common.model.page.exception.SortNotMatchedException

enum class Sort {
    ASC, DESC;

    companion object {
        fun from(value: String): Sort {
            return when (value.lowercase()) {
                "asc" -> ASC
                "desc" -> DESC
                else -> throw SortNotMatchedException(value)
            }
        }
    }
}
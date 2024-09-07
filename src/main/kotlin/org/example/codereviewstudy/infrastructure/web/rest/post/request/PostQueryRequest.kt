package org.example.codereviewstudy.infrastructure.web.rest.post.request

import org.example.codereviewstudy.common.model.page.Sort

data class PostQueryRequest(
    val id: Long? = null,
    val size: Int = 10,
    val sort: String = "desc",
) {
    val sortOrder: Sort
        get() = Sort.from(sort)
}

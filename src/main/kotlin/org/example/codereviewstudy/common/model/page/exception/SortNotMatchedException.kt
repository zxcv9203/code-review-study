package org.example.codereviewstudy.common.model.page.exception

import org.example.codereviewstudy.common.exception.NotMatchedException
import org.example.codereviewstudy.common.exception.message.ErrorMessage
import org.springframework.http.HttpStatus

class SortNotMatchedException(
    val input: String,
    override val message: String = ErrorMessage.NOT_MATCHED_SORT_VALUE.message
) : NotMatchedException(
    status = HttpStatus.BAD_REQUEST,
    message = message,
    actualValue = input,
) {
    override val errorMessage: String
        get() = "$message : 입력 값: $input"
}
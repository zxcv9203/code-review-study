package org.example.codereviewstudy.common.model.page

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.example.codereviewstudy.common.exception.message.ErrorMessage
import org.example.codereviewstudy.common.model.page.exception.SortNotMatchedException

class SortTest : DescribeSpec({

    describe("from") {
        context("value가 asc인 경우") {
            it("ASC를 반환한다.") {
                Sort.from("asc") shouldBe Sort.ASC
            }
        }

        context("value가 desc인 경우") {
            it("DESC를 반환한다.") {
                Sort.from("desc") shouldBe Sort.DESC
            }
        }

        context("value가 asc나 desc가 아닌 경우") {
            it("SortNotMatchedException을 던진다.") {
                val exception = shouldThrow<SortNotMatchedException> {
                    Sort.from("invalid")
                }
                exception.input shouldBe "invalid"
                exception.message shouldBe ErrorMessage.NOT_MATCHED_SORT_VALUE.message
                exception.errorMessage shouldBe "${ErrorMessage.NOT_MATCHED_SORT_VALUE.message} : 입력 값: ${exception.input}"
            }
        }
    }
})

package org.example.codereviewstudy.common.exception.handler

import org.example.codereviewstudy.common.exception.BusinessException
import org.example.codereviewstudy.common.exception.model.ValidationError
import org.example.codereviewstudy.common.model.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.ConversionNotSupportedException
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.ServletRequestBindingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler(
    private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
) {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        log.warn(e.errorMessage)
        log.debug(e.stackTraceToString())

        val response = ApiResponse.error(
            e.status,
            e.message
        )

        return ResponseEntity.status(e.status)
            .body(response)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ApiResponse<Unit>> {
        log.warn("$e")

        val response = ApiResponse.error(
            HttpStatus.NOT_FOUND,
            "요청한 리소스를 찾을 수 없습니다."
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: BindException): ResponseEntity<ApiResponse<List<ValidationError>>> {
        log.warn("Validation Error $e")

        val errors = e.bindingResult.allErrors.map { error ->
            val fieldError = error as FieldError
            ValidationError(fieldError.field, fieldError.defaultMessage ?: "Unknown error")
        }

        val response = ApiResponse.error(
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다.",
            errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(response)
    }

    @ExceptionHandler(
        HttpRequestMethodNotSupportedException::class,
        HttpMediaTypeNotSupportedException::class,
        HttpMediaTypeNotAcceptableException::class,
        MissingPathVariableException::class,
        MissingServletRequestParameterException::class,
        MissingServletRequestPartException::class,
        ServletRequestBindingException::class,
        HandlerMethodValidationException::class,
        NoHandlerFoundException::class,
        AsyncRequestTimeoutException::class,
        ErrorResponseException::class,
        MaxUploadSizeExceededException::class,
        ConversionNotSupportedException::class,
        TypeMismatchException::class,
        HttpMessageNotReadableException::class,
        HttpMessageNotWritableException::class,
        MethodValidationException::class,
        BindException::class
    )
    fun handleHttpRequest(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.warn("$e")

        val response = ApiResponse.error(
            HttpStatus.BAD_REQUEST,
            "잘못된 요청입니다."
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("서버 에러 : ", e)

        val response = ApiResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 에러가 발생했습니다."
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response)
    }
}
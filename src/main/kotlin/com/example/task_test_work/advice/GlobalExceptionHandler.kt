package com.example.task_test_work.advice

import com.example.task_test_work.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.resource.NoResourceFoundException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ServerWebInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NoSuchElementException): ErrorResponse {
        return ErrorResponse(
            status = 404,
            message = ex.message ?: "Element not found"
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoResourceFound(ex: Exception): ErrorResponse {

        return ErrorResponse(
            status = 404,
            message = ex.message ?: "Resource not found"
        )
    }

    @ExceptionHandler(MethodNotAllowedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleMethodNotAllowed(ex: MethodNotAllowedException): ErrorResponse {

        log.warn("[MethodNotAllowedException] {}", ex.printStackTrace())

        return ErrorResponse(
            status = 405,
            message = "Not supported method"
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(ex: IllegalArgumentException): ErrorResponse {

        log.error("[IllegalArgumentException] {}", ex.printStackTrace())

        return ErrorResponse(
            status = 400,
            message = ex.message ?: "Bad request"
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(ex: WebExchangeBindException): ErrorResponse {

        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ErrorResponse(
            status = 400,
            message = message
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequest(ex: ServerWebInputException): ErrorResponse {

        log.warn("[ServerWebInputException] {}", ex.printStackTrace())

        return ErrorResponse(
            status = 400,
            message = ex.message
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(ex: Exception): ErrorResponse {

        log.error("Internal error {}", ex.printStackTrace())

        return ErrorResponse(
            status = 500,
            message = "Internal server error"
        )
    }
}
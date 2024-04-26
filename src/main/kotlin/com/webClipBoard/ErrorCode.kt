package com.webClipBoard

import org.springframework.http.HttpStatus


enum class ErrorCode(val httpStatus: HttpStatus, val code: String) {
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN"),
}

data class ErrorResponse(val code: String)
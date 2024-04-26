package com.webClipBoard.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.webClipBoard.ErrorCode
import com.webClipBoard.ErrorResponse
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class ExceptionHandlerFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            setErrorResponse(response, ErrorCode.TOKEN_EXPIRED)
        }
    }

    private fun setErrorResponse(response: HttpServletResponse, errorCode: ErrorCode) {
        val objectMapper = ObjectMapper()
        response.status = errorCode.httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse: ErrorResponse = ErrorResponse(errorCode.code)
        try {
            response.writer.write(objectMapper.writeValueAsString(errorResponse))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
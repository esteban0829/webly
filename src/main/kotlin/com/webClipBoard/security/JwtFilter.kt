package com.webClipBoard.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtFilter(
    private val tokenProvider: TokenProvider
) : GenericFilterBean() {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpServletRequest = servletRequest as HttpServletRequest
        val jwt = resolveToken(httpServletRequest)
        if (!jwt.isNullOrBlank()) {
            val authentication = tokenProvider.getAuthentication(jwt)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(httpServletRequest, servletResponse)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearToken = request.getHeader("Authorization")
        if (bearToken?.startsWith("Bearer ") == true) {
            return bearToken.substring("Bearer ".length)
        }
        return null
    }
}
package com.webClipBoard.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.core.oidc.user.OidcUser

@Component
class KeycloakLogoutHandler(private val restTemplate: RestTemplate) : LogoutHandler {
    override fun logout(
        request: HttpServletRequest?, response: HttpServletResponse?,
        auth: Authentication
    ) {
        logoutFromKeycloak(auth.getPrincipal() as OidcUser)
    }

    private fun logoutFromKeycloak(user: OidcUser) {
        val endSessionEndpoint: String = user.issuer.toString() + "/protocol/openid-connect/logout"
        val builder = UriComponentsBuilder
            .fromUriString(endSessionEndpoint)
            .queryParam("id_token_hint", user.getIdToken().getTokenValue())
        val logoutResponse = restTemplate.getForEntity(
            builder.toUriString(), String::class.java
        )
        if (logoutResponse.statusCode.is2xxSuccessful) {
            logger.info("Successfulley logged out from Keycloak")
        } else {
            logger.error("Could not propagate logout to Keycloak")
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(KeycloakLogoutHandler::class.java)
    }
}
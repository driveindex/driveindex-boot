package io.github.driveindex.security.handler

import io.github.driveindex.Application
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.toJwtTag
import io.github.driveindex.dto.resp.admin.LoginRespDto
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.write
import io.github.driveindex.security.SecurityConfig
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/2/7 15:41
 */
@Component
class IAuthenticationSuccessHandler: AuthenticationSuccessHandler {
    private val secretKey: Key = Keys.hmacShaKeyFor(ConfigManager.getTokenSecurityKey())

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = Jwts.claims().let { claims ->
            claims.issuer = Application.APPLICATION_BASE_NAME
            val now = Date().time / 1000 * 1000
            claims[SecurityConfig.JWT_TAG] = ConfigManager.Password.toJwtTag(now)
            return@let Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date(now))
                .setExpiration(Date(now + ConfigManager.TokenExpired))
                .signWith(secretKey)
                .compact()
        }
        response.write(LoginRespDto(token).resp())
    }
}
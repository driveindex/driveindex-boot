package io.github.driveindex.security.filter

import io.github.driveindex.Application
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.log
import io.github.driveindex.security.PasswordOnlyToken
import io.github.driveindex.security.SecurityConfig
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import java.security.Key
import java.util.*


/**
 * @author sgpublic
 * @Date 2023/2/8 9:28
 */
@Component
class JwtTokenAuthenticationFilter: GenericFilterBean() {
    private val parser: JwtParser
    init {
        val secretKey: Key = Keys.hmacShaKeyFor(ConfigManager.getTokenSecurityKey())
        parser = Jwts.parserBuilder().setSigningKey(secretKey).build()
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        request as HttpServletRequest
        response as HttpServletResponse

        request.requestURI.let {
            if (it.startsWith("/api/login") || !it.startsWith("/api/admin")) {
                chain.doFilter(request, response)
                return
            }
        }
        (request.getHeader(SecurityConfig.Header) ?: "").takeIf {
            it.isNotBlank() && it.startsWith("Bearer ")
        }?.substring(7)?.let {
            val password = ConfigManager.Password
            try {
                val claims = parser.parseClaimsJws(it).body
                if (claims.expiration.before(Date())) {
                    log.debug("token 过期")
                    return@let
                }
                if (claims.issuer != Application.APPLICATION_BASE_NAME) {
                    log.debug("未知的 token 签发者")
                    return@let
                }
                SecurityContextHolder.getContext().authentication =
                        PasswordOnlyToken.authenticated(password, SecurityConfig.AUTH_ADMIN)
            } catch (e: Exception) {
                log.debug("jwt 未知错误", e)
            }
        }
        chain.doFilter(request, response)
    }
}
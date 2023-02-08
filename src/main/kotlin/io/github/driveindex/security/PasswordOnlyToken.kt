package io.github.driveindex.security

import io.github.driveindex.Application
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

/**
 * @author sgpublic
 * @Date 2023/2/7 15:37
 */
class PasswordOnlyToken private constructor(
    password: String, authorities: Collection<GrantedAuthority>? = null
): UsernamePasswordAuthenticationToken(
    Application.APPLICATION_BASE_NAME, password, authorities
) {
    override fun getCredentials(): String {
        return super.getCredentials() as String
    }

    companion object {
        fun unauthenticated(password: String): PasswordOnlyToken {
            return PasswordOnlyToken(password)
        }

        fun authenticated(password: String, authorities: Collection<GrantedAuthority>): PasswordOnlyToken {
            return PasswordOnlyToken(password, authorities)
        }
    }
}
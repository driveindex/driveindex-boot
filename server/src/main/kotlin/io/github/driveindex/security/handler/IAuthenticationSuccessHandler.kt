package io.github.driveindex.security.handler

import io.github.driveindex.dto.resp.admin.LoginRespDto
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.write
import io.github.driveindex.h2.dao.UserDao
import io.github.driveindex.security.UserPasswordToken
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

/**
 * @author sgpublic
 * @Date 2023/2/7 15:41
 */
@Component
class IAuthenticationSuccessHandler(
    private val user: UserDao
): AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        authentication as UserPasswordToken

        val username = authentication.principal
        val entity = user.getValidUser(username)
            ?: throw IllegalStateException("user not found: $username")

        response.write(LoginRespDto(
            username = entity.username,
            nick = entity.nick,
            auth = LoginRespDto.Auth(
                token = authentication.createJwtToken(),
                role = entity.role,
            )
        ).resp())
    }
}
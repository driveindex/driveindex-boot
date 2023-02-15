package io.github.driveindex.security.handler

import io.github.driveindex.core.util.log
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.exception.WrongPasswordException
import io.github.driveindex.exception.write
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

/**
 * @author sgpublic
 * @Date 2023/2/7 16:05
 */
@Component
class IAuthenticationFailureHandler: AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        log.debug("登录失败", exception)
        when (exception) {
            is WrongPasswordException -> FailedResult.WrongPassword
            is AuthenticationServiceException -> FailedResult.UnsupportedRequest
            else -> FailedResult.InternalServerError
        }.let {
            response.write(it)
        }
    }
}
package io.github.driveindex.controller

import io.github.driveindex.core.util.log
import io.github.driveindex.exception.FailedResult
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * @author sgpublic
 * @Date 2023/2/8 9:19
 */
@RestControllerAdvice
class ExceptionHandler {
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(FailedResult::class)
    fun handleFailedResult(e: FailedResult, resp: HttpServletResponse): FailedResult {
        log.debug("响应错误信息", e)
        return e
    }
}
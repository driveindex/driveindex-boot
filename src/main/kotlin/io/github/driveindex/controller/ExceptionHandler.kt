package io.github.driveindex.controller

import io.github.driveindex.core.util.log
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.exception.FailedResult
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
    fun handleFailedResult(e: FailedResult): RespResult<Nothing> {
        log.debug("响应错误信息", e)
        return RespResult(e.code, e.message)
    }

    @ExceptionHandler(NotImplementedError::class)
    fun handleNotImplementedError(e: NotImplementedError): RespResult<Nothing> {
        return handleFailedResult(FailedResult.NotImplementationError)
    }
}
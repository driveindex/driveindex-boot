package io.github.driveindex.controller

import io.github.driveindex.dto.req.admin.LoginReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.admin.LoginRespDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author sgpublic
 * @Date 2023/2/7 13:42
 */
@RestController
@Tag(name = "管理员登陆")
class AdminController {
    /**
     * 利用 SpringSecurity 检查登录是否有效
     * @return 若登录有效直接返回 code 200
     */
    @Operation(
        summary = "token 有效性检查",
        description = "检查 token 是否有效",
        security = [SecurityRequirement(name = "Authentication")]
    )
    @GetMapping("/api/admin/token_state")
    fun checkToken(): RespResult<Nothing> {
        return RespResult.SAMPLE
    }

    @Operation(summary = "管理员登陆", description = "使用密码登陆")
    @PostMapping("/api/login")
    fun login(
        @RequestBody dto: LoginReqDto
    ): RespResult<LoginRespDto> {
        throw BadCredentialsException("use authentication provider instead.")
    }
}
package io.github.driveindex.controller

import io.github.driveindex.core.ConfigManager
import io.github.driveindex.dto.common.admin.CommonSettings
import io.github.driveindex.dto.req.admin.LoginReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.admin.LoginRespDto
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.security.SecurityConfig
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
@Tag(name = "用户接口")
class UserController {
    @Operation(summary = "常规设置", description = "常规设置")
    @GetMapping("/api/user/common")
    fun getCommonSettings(): RespResult<CommonSettings> {
        return CommonSettings(
                deltaTick = ConfigManager.DeltaTrackingTick,
                corsOrigin = ConfigManager.CorsOrigins,
        ).resp()
    }

    @Operation(summary = "常规设置", description = "常规设置")
    @PostMapping("/api/user/common")
    fun setCommonSettings(
            @RequestBody dto: CommonSettings
    ): RespResult<Nothing> {
        if (dto.deltaTick < 0) {
            throw FailedResult.CommonSettings.DeltaTrackDuration
        }
        ConfigManager.DeltaTrackingTick = dto.deltaTick
        ConfigManager.CorsOrigins = dto.corsOrigin
        SecurityConfig.updateDownloadCors()
        return RespResult.SAMPLE
    }


    @Operation(summary = "枚举 Client 配置", description = "获取所有 Client 配置", )
    @PostMapping("/api/user/clients")
    fun listClients() {

    }
}
package io.github.driveindex.controller

import io.github.driveindex.core.util.SHA1
import io.github.driveindex.dto.req.user.ClientCreateReqDto
import io.github.driveindex.dto.req.user.ClientEditReqDto
import io.github.driveindex.dto.req.user.SetCommonReqDto
import io.github.driveindex.dto.req.user.SetPwdReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.admin.CommonSettingsRespDto
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.module.Current
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author sgpublic
 * @Date 2023/2/7 13:42
 */
@RestController
@Tag(name = "用户接口")
class UseConfController(
    private val current: Current,
    private val clientsDao: ClientsDao,
) {
    @Operation(summary = "修改密码")
    @GetMapping("/api/user/password")
    fun setPassword(@RequestBody dto: SetPwdReqDto): RespResult<Nothing> {
        val config = current.User
        if (config.password != dto.oldPwd.SHA1) {
            throw FailedResult.UserSettings.PasswordNotMatched
        }
        if (dto.newPwd.length < 6) {
            throw FailedResult.UserSettings.PasswordLength
        }
        current.User = config.also {
            it.password = dto.newPwd.SHA1
        }
        return RespResult.SAMPLE
    }

    @Operation(summary = "常规设置", description = "常规设置")
    @GetMapping("/api/user/common")
    fun getCommonSettings(): RespResult<CommonSettingsRespDto> {
        val config = current.UserConfig
        return CommonSettingsRespDto(
            deltaTick = config.deltaTick,
            corsOrigin = config.corsOrigin,
        ).resp()
    }

    @Operation(summary = "常规设置", description = "常规设置")
    @PostMapping("/api/user/common")
    fun setCommonSettings(
            @RequestBody dto: SetCommonReqDto
    ): RespResult<Nothing> {
        if (dto.deltaTick < 0) {
            throw FailedResult.UserSettings.DeltaTrackDuration
        }
        current.UserConfig = current.UserConfig.also {
            it.deltaTick = dto.deltaTick
            it.corsOrigin = dto.corsOrigin
        }
        return RespResult.SAMPLE
    }


    @Operation(summary = "枚举 Client 配置", description = "获取所有 Client 配置")
    @GetMapping("/api/user/clients")
    fun listClients() {

    }

    @Operation(summary = "枚举 Client 配置", description = "获取所有 Client 配置")
    @GetMapping("/api/user/accounts")
    fun listAccount(@RequestParam("client_id") clientId: String) {

    }


    @Operation(summary = "创建 Client")
    @PostMapping("/api/user/client")
    fun createClient(@RequestBody dto: ClientCreateReqDto): RespResult<Nothing> {
        dto.type.create(dto.data)
        return RespResult.SAMPLE
    }

    @Operation(summary = "修改 Client")
    @PutMapping("/api/user/client")
    fun editClient(@RequestBody dto: ClientEditReqDto): RespResult<Nothing> {
        dto.type.edit(dto.data, dto.clientId)
        return RespResult.SAMPLE
    }
}
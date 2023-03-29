package io.github.driveindex.controller

import io.github.driveindex.client.ClientType
import io.github.driveindex.core.util.SHA1
import io.github.driveindex.dto.req.user.ClientCreateReqDto
import io.github.driveindex.dto.req.user.ClientEditReqDto
import io.github.driveindex.dto.req.user.SetCommonReqDto
import io.github.driveindex.dto.req.user.SetPwdReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.SampleResult
import io.github.driveindex.dto.resp.admin.CommonSettingsRespDto
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.dto.resp.user.AccountsDto
import io.github.driveindex.dto.resp.user.ClientsDto
import io.github.driveindex.dto.resp.user.OneDriveAccountDetail
import io.github.driveindex.dto.resp.user.OneDriveClientDetail
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.AccountsDao
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.h2.dao.OneDriveAccountDao
import io.github.driveindex.h2.dao.OneDriveClientDao
import io.github.driveindex.module.Current
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/2/7 13:42
 */
@RestController
@Tag(name = "用户接口")
class UseConfController(
    private val current: Current,
    private val clientsDao: ClientsDao,
    private val accountsDao: AccountsDao,

    private val onedriveClientDao: OneDriveClientDao,
    private val onedriveAccountDao: OneDriveAccountDao,
) {
    @Operation(summary = "修改密码")
    @GetMapping("/api/user/password")
    fun setPassword(@RequestBody dto: SetPwdReqDto): SampleResult {
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
        return SampleResult
    }

    @Operation(summary = "常规设置")
    @GetMapping("/api/user/common")
    fun getCommonSettings(): RespResult<CommonSettingsRespDto> {
        val config = current.UserConfig
        return CommonSettingsRespDto(
            deltaTick = config.deltaTick,
            corsOrigin = config.corsOrigin,
        ).resp()
    }

    @Operation(summary = "常规设置")
    @PostMapping("/api/user/common")
    fun setCommonSettings(
            @RequestBody dto: SetCommonReqDto
    ): SampleResult {
        if (dto.deltaTick < 0) {
            throw FailedResult.UserSettings.DeltaTrackDuration
        }
        current.UserConfig = current.UserConfig.also {
            it.deltaTick = dto.deltaTick
            it.corsOrigin = dto.corsOrigin
        }
        return SampleResult
    }


    @Operation(summary = "枚举 Client 配置")
    @GetMapping("/api/user/clients")
    fun listClients(): RespResult<List<ClientsDto<*>>> {
        val list: ArrayList<ClientsDto<*>> = ArrayList()
        for (entity in clientsDao.listByUser(current.User.id)) {
            list.add(ClientsDto(
                id = entity.id,
                name = entity.name,
                type = entity.type,
                createAt = entity.createAt,
                modifyAt = entity.modifyAt,
                detail = when (entity.type) {
                    ClientType.OneDrive ->
                        onedriveClientDao.getReferenceById(entity.id).let {
                            return@let OneDriveClientDetail(
                                clientId = it.clientId,
                                tenantId = it.tenantId,
                                endPoint = it.endPoint,
                            )
                        }
                }
            ))
        }
        return list.resp()
    }

    @Operation(summary = "枚举 Client 下登录的账号")
    @GetMapping("/api/user/accounts")
    fun listAccount(@RequestParam("client_id") clientId: UUID): RespResult<List<AccountsDto<*>>> {
        val client = clientsDao.getClient(clientId)
            ?: throw FailedResult.Client.NotFound
        val list: ArrayList<AccountsDto<*>> = ArrayList()
        for (entity in accountsDao.listByClient(client.id)) {
            list.add(AccountsDto(
                id = entity.id,
                displayName = entity.displayName,
                userPrincipalName = entity.userPrincipalName,
                createAt = entity.createAt,
                modifyAt = entity.modifyAt,
                detail = when (client.type) {
                    ClientType.OneDrive ->
                        onedriveAccountDao.getReferenceById(entity.id).let {
                            return@let OneDriveAccountDetail(
                                azureUserId = it.azureUserId,
                            )
                        }
                }
            ))
        }
        return list.resp()
    }


    @Operation(summary = "创建 Client")
    @PostMapping("/api/user/client")
    fun createClient(@RequestBody dto: ClientCreateReqDto): SampleResult {
        dto.type.create(dto.name, dto.data)
        return SampleResult
    }

    @Operation(summary = "修改 Client")
    @PutMapping("/api/user/client")
    fun editClient(@RequestBody dto: ClientEditReqDto): SampleResult {
        dto.type.edit(dto.data, dto.clientId)
        return SampleResult
    }
}
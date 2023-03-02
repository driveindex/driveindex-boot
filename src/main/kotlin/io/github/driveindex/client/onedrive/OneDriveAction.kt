package io.github.driveindex.client.onedrive

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientAction
import io.github.driveindex.client.ClientType
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.*
import io.github.driveindex.dto.req.user.ClientLoginReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.feigh.AzurePortalClient
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.h2.dao.OneDriveAccountDao
import io.github.driveindex.h2.dao.OneDriveClientDao
import io.github.driveindex.h2.entity.ClientsEntity
import io.github.driveindex.h2.entity.OneDriveAccountEntity
import io.github.driveindex.h2.entity.OneDriveClientEntity
import io.github.driveindex.module.Current
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OneDriveAction(
    private val current: Current,
    override val clientDao: ClientsDao,
    private val onedriveClientDao: OneDriveClientDao,
    private val oneDriveAccountDao: OneDriveAccountDao,
): ClientAction {
    override val type: ClientType = ClientType.OneDrive

    @GetMapping("/api/user/login/url/onedrive")
    override fun loginUri(@RequestBody dto: ClientLoginReqDto): RespResult<String> {
        val client = getClient(dto.clientId)
        onedriveClientDao.getOneDriveClient(dto.clientId).let { entity ->
            val state = linkedMapOf<String, Any>(
                "id" to dto.clientId,
                "type" to client.type,
                "ts" to System.currentTimeMillis()
            )
            state["sign"] = "${state.joinToSortedString("&")}${ConfigManager.TokenSecurityKey}".TO_BASE64
            return ("${entity.endPoint.LoginHosts}/${entity.tenantId}/oauth2/v2.0/authorize?" +
                "client_id=${entity.clientId}" +
                "&response_type=code" +
                "&redirect_uri=${dto.redirectUri}" +
                "&response_mode=query" +
                "&scope=${AzurePortalClient.Scope.joinToString("%20")}" +
                "&state=${state.joinToString("&").TO_BASE64}").resp()
        }
    }

    @PostMapping("/api/user/login/request/onedrive")
    override fun loginRequest(@RequestBody params: JsonObject): RespResult<Nothing> {
        val param = params.get("state")
            .asString.ORIGIN_BASE64
            .split("&")
            .fold(mutableMapOf<String, String>()) { map, param ->
                param.split("=").takeIf {
                    it.size == 2
                }?.let {
                    map[it[0]] = it[1]
                }
                return@fold map
            }
        val ts = param["ts"]?.toLongOrNull()
            ?: throw FailedResult.Auth.IllegalRequest
        if (ts + 600L * 1000 < System.currentTimeMillis()) {
            throw FailedResult.Auth.AuthTimeout
        }

        val client = try {
            UUID.fromString(param["id"])
        } catch (e: Exception) {
            null
        }?.let { clientId ->
            val client = clientDao.getClient(clientId)
                ?: throw FailedResult.Auth.IllegalRequest
            param["type"]?.let type@{
                return@type ClientType.valueOf(it)
            }?.takeIf {
                client.type == it
            } ?: throw FailedResult.Auth.IllegalRequest
            return@let onedriveClientDao.getOneDriveClient(client.id)
        } ?: throw FailedResult.Auth.IllegalRequest

        val token = client.endPoint.Portal.getToken(
            client.tenantId,
            param["code"] ?: throw FailedResult.Auth.IllegalRequest,
            client.clientSecret
        )

        val me = client.endPoint.Graph.Me(token.tokenStr)
        oneDriveAccountDao.findByAzureId(me.id)
            ?: throw FailedResult.Auth.DuplicateAccount

        oneDriveAccountDao.save(OneDriveAccountEntity(
            parentClientId = client.id,
            azureId = UUID.fromString(me.id),
            displayName = me.displayName,
            userPrincipalName = me.userPrincipalName,
            tokenType = token.tokenType,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
            tokenExpire = token.expires
        ))

        return RespResult.SAMPLE
    }

    @Transactional
    override fun create(params: JsonObject) {
        val creation = ClientCreateOneDrive::class.fromGson(params)
        clientDao.findClient(creation.name)?.let {
            throw FailedResult.Client.DuplicateClientName
        }

        onedriveClientDao.findClient(
            creation.azureClientId,
            creation.azureClientSecret,
            creation.endPoint,
            creation.tenantId,
        )?.let {
            throw FailedResult.Client.DuplicateClientInfo(creation.name, it.id)
        }

        val client = ClientsEntity(
            name = creation.name,
            type = ClientType.OneDrive,
        )
        clientDao.save(client)
        onedriveClientDao.save(OneDriveClientEntity(
            id = client.id,
            clientId = creation.azureClientId,
            clientSecret = creation.azureClientSecret,
            tenantId = creation.tenantId,
            endPoint = creation.endPoint,
            createBy = current.User.id,
            createAt = System.currentTimeMillis(),
        ))
    }

    @Transactional
    override fun edit(params: JsonObject, clientId: UUID) {
        getClient(clientId)
        val edition = ClientEditOneDrive::class.fromGson(params)
        clientDao.getClient(clientId)?.also {
            edition.name?.let { name ->
                if (it.name == name) {
                    return@also
                }
                it.name = name
            }
            clientDao.save(it)
        } ?: FailedResult.Client.NotFound
        onedriveClientDao.getOneDriveClient(clientId).also {
            edition.clientSecret?.let { secret ->
                if (it.clientSecret == secret) {
                    return@also
                }
                it.clientSecret = secret
            }
            onedriveClientDao.save(it)
        }
    }

    data class ClientCreateOneDrive(
        @RequestParam(required = true)
        val name: String,
        @RequestParam(required = true)
        val azureClientId: String,
        @RequestParam(required = true)
        val azureClientSecret: String,
        val endPoint: OneDriveClientEntity.EndPoint = OneDriveClientEntity.EndPoint.Global,
        val tenantId: String = "common",
    )

    data class ClientEditOneDrive(
        val name: String?,
        val clientSecret: String?,
    )

    data class LoginReqDto(
        val code: String,
        val state: String,
    )
}
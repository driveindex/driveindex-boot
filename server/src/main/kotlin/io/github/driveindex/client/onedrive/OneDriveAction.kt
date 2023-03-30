package io.github.driveindex.client.onedrive

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientAction
import io.github.driveindex.client.ClientType
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.*
import io.github.driveindex.dto.feign.AzureGraphDtoV2_Me_Drive_Root_Delta
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.SampleResult
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.feigh.AzurePortalClient
import io.github.driveindex.h2.dao.AccountsDao
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.h2.dao.onedrive.OneDriveAccountDao
import io.github.driveindex.h2.dao.onedrive.OneDriveClientDao
import io.github.driveindex.h2.entity.AccountsEntity
import io.github.driveindex.h2.entity.ClientsEntity
import io.github.driveindex.h2.entity.onedrive.OneDriveAccountEntity
import io.github.driveindex.h2.entity.onedrive.OneDriveClientEntity
import io.github.driveindex.module.Current
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OneDriveAction(
    private val current: Current,
    override val clientDao: ClientsDao,
    private val accountDao: AccountsDao,

    private val onedriveClientDao: OneDriveClientDao,
    private val onedriveAccountDao: OneDriveAccountDao,
): ClientAction {
    override val type: ClientType = ClientType.OneDrive

    @GetMapping("/api/user/login/url/onedrive")
    override fun loginUri(
        @RequestParam("client_id") clientId: UUID,
        @RequestParam("redirect_uri") redirectUri: String
    ): RespResult<String> {
        val client = getClient(clientId)
        onedriveClientDao.getReferenceById(clientId).let { entity ->
            val state = linkedMapOf<String, Any>(
                "id" to clientId,
                "type" to client.type,
                "ts" to System.currentTimeMillis()
            )
            state["sign"] = "${state.joinToSortedString("&")}${ConfigManager.TokenSecurityKey}".TO_BASE64
            return ("${entity.endPoint.LoginHosts}/${entity.tenantId}/oauth2/v2.0/authorize?" +
                "client_id=${entity.clientId}" +
                "&response_type=code" +
                "&redirect_uri=${redirectUri}" +
                "&response_mode=query" +
                "&scope=${AzurePortalClient.Scope.joinToString("%20")}" +
                "&state=${state.joinToString("&").TO_BASE64}").resp()
        }
    }

    @PostMapping("/api/user/login/request/onedrive")
    override fun loginRequest(@RequestBody params: JsonObject): RespResult<Unit> {
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
            return@let onedriveClientDao.getReferenceById(client.id)
        } ?: throw FailedResult.Auth.IllegalRequest

        val token = client.endPoint.Portal.getToken(
            client.tenantId,
            param["code"] ?: throw FailedResult.Auth.IllegalRequest,
            client.clientSecret
        )

        val me = client.endPoint.Graph.Me(token.tokenStr)

        // 允许账号失效后重新登录
        onedriveAccountDao.findByAzureId(
            accountDao.findByClient(client.id), me.id
        )?.apply {
            tokenType = token.tokenType
            accessToken = token.accessToken
            refreshToken = token.refreshToken
            tokenExpire = token.expires
            accountExpired = false
            onedriveAccountDao.save(this)

            return SampleResult
        }

        // TODO 重名时自动重命名
        accountDao.findByName(client.id, me.displayName)?.let {
            throw FailedResult.Client.DuplicateAccountName(it.displayName, it.id)
        }

        val entity = AccountsEntity(
            parentClientId = client.id,
            displayName = me.displayName,
            createBy = current.User.id,
            userPrincipalName = me.userPrincipalName,
        )
        accountDao.save(entity)
        onedriveAccountDao.save(
            OneDriveAccountEntity(
                id = entity.id,
                azureUserId = me.id,
                tokenType = token.tokenType,
                accessToken = token.accessToken,
                refreshToken = token.refreshToken,
                tokenExpire = token.expires,
            )
        )

        return SampleResult
    }

    @Transactional
    override fun create(name: String, params: JsonObject) {
        val creation = ClientCreateOneDrive::class.fromGson(params)
        val user = current.User.id
        clientDao.findByName(user, name)?.let {
            throw FailedResult.Client.DuplicateClientName
        }

        onedriveClientDao.findClient(
            clientDao.findByUser(user),
            creation.azureClientId,
            creation.azureClientSecret,
            creation.endPoint,
            creation.tenantId,
        )?.let {
            throw FailedResult.Client.DuplicateClientInfo(name, it.id)
        }

        val client = ClientsEntity(
            name = name,
            type = ClientType.OneDrive,
            createBy = user,
            supportDelta = creation.endPoint.supportDelta,
        )
        clientDao.save(client)
        onedriveClientDao.save(
            OneDriveClientEntity(
                id = client.id,
                clientId = creation.azureClientId,
                clientSecret = creation.azureClientSecret,
                tenantId = creation.tenantId,
                endPoint = creation.endPoint,
            )
        )
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
        } ?: throw FailedResult.Client.NotFound
        onedriveClientDao.getReferenceById(clientId).also {
            edition.clientSecret?.let { secret ->
                if (it.clientSecret == secret) {
                    return@also
                }
                it.clientSecret = secret
            }
            onedriveClientDao.save(it)
        }
    }

    @Transactional
    override fun delta(accountId: UUID) {
        val endPoint = onedriveClientDao.getReferenceById(
            accountDao.getReferenceById(accountId).parentClientId
        ).endPoint
        var delta: AzureGraphDtoV2_Me_Drive_Root_Delta
        val token: String
        onedriveAccountDao.getReferenceById(accountId).let {
            token = it.accessToken
            delta = AzureGraphDtoV2_Me_Drive_Root_Delta(
                "token=${it.deltaToken ?: ""}", null, listOf()
            )
        }
        do {
            delta = endPoint.Graph.Me_Drive_Root_Delta(token, delta.nextToken)
            for (item in delta.value) {

            }
        } while (delta.deltaToken == null)
    }

    data class ClientCreateOneDrive(
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
}
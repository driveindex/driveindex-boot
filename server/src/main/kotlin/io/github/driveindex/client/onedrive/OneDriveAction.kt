package io.github.driveindex.client.onedrive

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
import io.github.driveindex.h2.dao.FileDao
import io.github.driveindex.h2.dao.onedrive.OneDriveAccountDao
import io.github.driveindex.h2.dao.onedrive.OneDriveClientDao
import io.github.driveindex.h2.dao.onedrive.OneDriveFileDao
import io.github.driveindex.h2.entity.AccountsEntity
import io.github.driveindex.h2.entity.ClientsEntity
import io.github.driveindex.h2.entity.FileEntity
import io.github.driveindex.h2.entity.onedrive.OneDriveAccountEntity
import io.github.driveindex.h2.entity.onedrive.OneDriveClientEntity
import io.github.driveindex.h2.entity.onedrive.OneDriveFileEntity
import io.github.driveindex.module.Current
import jakarta.transaction.Transactional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.driveindex.core.util.JsonGlobal
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.aspectj.weaver.tools.cache.SimpleCacheFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OneDriveAction(
    private val current: Current,
    override val clientDao: ClientsDao,
    private val accountDao: AccountsDao,
    private val fileDao: FileDao,

    private val onedriveClientDao: OneDriveClientDao,
    private val onedriveAccountDao: OneDriveAccountDao,
    private val onedriveFileDao: OneDriveFileDao,
): ClientAction {
    override val type: ClientType = ClientType.OneDrive

    @GetMapping("/api/user/login/url/onedrive")
    override fun loginUri(
        @RequestParam("client_id", required = true) clientId: UUID,
        @RequestParam("redirect_uri", required = true) redirectUri: String
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
        val param = JsonGlobal.decodeFromJsonElement<AccountLoginOneDrive>(params)
            .state.ORIGIN_BASE64
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
            accountDao.selectIdByClient(client.id), me.id
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
        val creation: ClientCreateOneDrive = JsonGlobal.decodeFromJsonElement(params)

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
        val edition: ClientEditOneDrive = JsonGlobal.decodeFromJsonElement(params)
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
    override fun delete(clientId: UUID) {
        onedriveClientDao.deleteById(clientId)
    }

    @Transactional
    override fun delta(accountId: UUID) {
        log.info("account delta track start! account id: $accountId")
        val endPoint = onedriveClientDao.getReferenceById(
            accountDao.getReferenceById(accountId).parentClientId
        ).endPoint
        var delta: AzureGraphDtoV2_Me_Drive_Root_Delta
        val token: String
        val account = onedriveAccountDao.getReferenceById(accountId).also {
            token = it.accessToken
            delta = AzureGraphDtoV2_Me_Drive_Root_Delta(
                "token=${it.deltaToken ?: ""}", null, listOf()
            )
        }
        do {
            delta = endPoint.Graph.Me_Drive_Root_Delta(token, delta.nextToken)
            for (item in delta.value) {
                val parent = onedriveFileDao.findByParentReference(
                    item.parentReference.id
                )
                fileDao.save(FileEntity(
                    accountId = account.id,
                    name = item.name,
                    parentId = parent?.id,
                    isDir = item.folder != null,
                    createBy = null,
                    path = parent?.id?.let {
                        return@let fileDao.findByIdOrNull(it)?.path
                    } ?: CanonicalPath.ROOT,
                    clientType = type
                ))
                onedriveFileDao.save(OneDriveFileEntity(
                    accountId = account.id,
                    fileId = item.id,
                    webUrl = item.webUrl,
                    mimeType = item.file?.mimeType ?: "directory",
                    quickXorHash = item.file?.hashes?.quickXorHash,
                    sha1Hash = item.file?.hashes?.sha1Hash,
                    sha256Hash = item.file?.hashes?.sha256Hash,
                ))
            }
        } while (delta.deltaToken == null)
        account.deltaToken = delta.deltaToken
        log.info("account delta track finished! account id: $accountId")
    }

    @Serializable
    data class AccountLoginOneDrive(
        val state: String,
    )

    @Serializable
    data class ClientCreateOneDrive(
        @SerialName("azure_client_id")
        val azureClientId: String,
        @SerialName("azure_client_secret")
        val azureClientSecret: String,
        @SerialName("end_point")
        val endPoint: OneDriveClientEntity.EndPoint = OneDriveClientEntity.EndPoint.Global,
        @SerialName("tenant_id")
        val tenantId: String = "common",
    )

    @Serializable
    data class ClientEditOneDrive(
        @SerialName("name")
        val name: String?,
        @SerialName("client_secret")
        val clientSecret: String?,
    )
}


/**
 * 扩展：转为 Microsoft Graph 接口中需要的路径参数。
 * <br/>- 若当前路径为根目录，则返回空文本；
 * <br/>- 若当前路径为不根目录，则在首位添加英文冒号返回，即：":${CanonicalPath#getPath()}:"。
 * @return 转换后的文本
 */
fun CanonicalPath.toAzureCanonicalizePath(): String {
    val canonicalizePath = SimpleCacheFactory.path
    return if (CanonicalPath.ROOT_PATH == canonicalizePath) "" else ":$canonicalizePath:"
}
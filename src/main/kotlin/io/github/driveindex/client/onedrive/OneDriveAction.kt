package io.github.driveindex.client.onedrive

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientAction
import io.github.driveindex.client.ClientType
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.*
import io.github.driveindex.dto.req.user.ClientLoginReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.dto.resp.resp
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.h2.dao.OneDriveClientDao
import io.github.driveindex.h2.entity.ClientsEntity
import io.github.driveindex.h2.entity.OneDriveClientEntity
import jakarta.transaction.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class OneDriveAction(
    override val clientDao: ClientsDao,
    private val onedriveClientDao: OneDriveClientDao,
): ClientAction {
    override val type: ClientType = ClientType.OneDrive

    private val scope: Array<String> = arrayOf(
        "Files.ReadWrite",
        "Files.ReadWrite.All",
        "offline_access",
        "Sites.ReadWrite.All",
    )
    @GetMapping("/api/user/login/url/onedrive")
    override fun loginUri(@RequestBody dto: ClientLoginReqDto): RespResult<String> {
        val client = getClient(dto.clientId)
        onedriveClientDao.getOneDriveClient(dto.clientId).let { entity ->
            val state = linkedMapOf<String, Any>(
                "id" to dto.clientId,
                "type" to client.type,
            )
            state["sign"] = "${state.joinToSortedString("&")}${ConfigManager.TokenSecurityKey}".TO_BASE64
            return ("${entity.endPoint.Login}/${entity.tenantId}/oauth2/v2.0/authorize?" +
                "client_id=${entity.clientId}" +
                "&response_type=code" +
                "&redirect_uri=${dto.redirectUri}" +
                "&response_mode=query" +
                "&scope=${scope.joinToString("%20")}" +
                "&state=${state.joinToString("&").TO_BASE64}").resp()
        }
    }

    @PostMapping("/api/user/login/request/onedrive")
    override fun loginRequest(@RequestBody params: JsonObject) {

    }

    @Transactional
    override fun create(params: JsonObject) {
        val creation = ClientCreateOneDrive::class.fromGson(params)
        val client = ClientsEntity(type = ClientType.OneDrive)
        clientDao.save(client)
        onedriveClientDao.save(OneDriveClientEntity(
            id = client.id,
            clientId = creation.azureClientId,
            clientSecret = creation.azureClientSecret,
            tenantId = creation.tenantId,
            endPoint = creation.endPoint,
        ))
    }

    @Transactional
    override fun edit(params: JsonObject, clientId: UUID) {
        getClient(clientId)
        val edition = ClientEditOneDrive::class.fromGson(params)
        onedriveClientDao.getOneDriveClient(clientId).also {
            it.clientSecret = edition.clientSecret
            onedriveClientDao.save(it)
        }
    }

    data class ClientCreateOneDrive (
        @RequestParam(required = true)
        val azureClientId: String,
        @RequestParam(required = true)
        val azureClientSecret: String,
        val endPoint: OneDriveClientEntity.EndPoint,
        val tenantId: String,
    )

    data class ClientEditOneDrive (
        val clientSecret: String,
    )

    data class LoginReqDto(
        val code: String,
        val state: String,
    )
}
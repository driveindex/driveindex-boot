package io.github.driveindex.client

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import io.github.driveindex.Application
import io.github.driveindex.Application.Companion.Bean
import io.github.driveindex.client.onedrive.OneDriveAction
import io.github.driveindex.dto.req.user.ClientLoginReqDto
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.dao.ClientsDao
import io.github.driveindex.h2.entity.ClientsEntity
import jakarta.annotation.PostConstruct
import java.util.*
import kotlin.reflect.KClass

interface ClientAction {
    fun loginUri(clientId: UUID, redirectUri: String): RespResult<String>
    fun loginRequest(params: JsonObject): RespResult<Unit>

    fun onConstruct() { }

    val clientDao: ClientsDao
    val type: ClientType
    fun getClient(id: UUID): ClientsEntity {
        val client = Application.getBean<ClientsDao>().getClient(id)
            ?: throw FailedResult.Client.NotFound
        if (client.type != type) {
            throw FailedResult.Client.TypeNotMatch
        }
        return client
    }

    fun create(name: String, params: JsonObject)
    fun edit(params: JsonObject, clientId: UUID)

    fun delta(accountId: UUID)
}

enum class ClientType(
    private val target: KClass<out ClientAction>
): ClientAction {
    @SerializedName("onedrive")
    OneDrive(OneDriveAction::class);

    private val action: ClientAction by lazy { target.Bean }

    override val clientDao: ClientsDao get() = action.clientDao
    override val type: ClientType get() = action.type

    override fun loginUri(clientId: UUID, redirectUri: String): RespResult<String> {
        return action.loginUri(clientId, redirectUri)
    }

    override fun loginRequest(params: JsonObject): RespResult<Unit> {
        return action.loginRequest(params)
    }

    override fun create(name: String, params: JsonObject) {
        action.create(name, params)
    }

    override fun edit(params: JsonObject, clientId: UUID) {
        action.edit(params, clientId)
    }

    override fun delta(accountId: UUID) {
        action.delta(accountId)
    }

    @PostConstruct
    override fun onConstruct() {
        action.onConstruct()
    }
}
package io.github.driveindex.client

import io.github.driveindex.Application
import io.github.driveindex.Application.Companion.Bean
import io.github.driveindex.client.onedrive.OneDriveAction
import io.github.driveindex.core.util.KUUID
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.database.dao.ClientsDao
import io.github.driveindex.database.entity.ClientsEntity
import jakarta.annotation.PostConstruct
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KClass

interface ClientAction {
    fun loginUri(clientId: KUUID, redirectUri: String): RespResult<String>
    fun loginRequest(params: JsonObject): RespResult<Unit>

    fun onConstruct() { }

    val clientDao: ClientsDao
    val type: ClientType
    fun getClient(id: KUUID): ClientsEntity {
        val client = Application.getBean<ClientsDao>().getClient(id)
            ?: throw FailedResult.Client.NotFound
        if (client.type != type) {
            throw FailedResult.Client.TypeNotMatch
        }
        return client
    }

    fun create(name: String, params: JsonObject)
    fun delete(clientId: KUUID)
    fun edit(params: JsonObject, clientId: KUUID)

    fun delta(accountId: KUUID)
}

enum class ClientType(
    private val target: KClass<out ClientAction>
): ClientAction {
    OneDrive(OneDriveAction::class);

    private val action: ClientAction by lazy { target.Bean }

    override val clientDao: ClientsDao get() = action.clientDao
    override val type: ClientType get() = action.type

    override fun loginUri(clientId: KUUID, redirectUri: String): RespResult<String> {
        return action.loginUri(clientId, redirectUri)
    }

    override fun loginRequest(params: JsonObject): RespResult<Unit> {
        return action.loginRequest(params)
    }

    override fun create(name: String, params: JsonObject) {
        action.create(name, params)
    }

    override fun edit(params: JsonObject, clientId: KUUID) {
        action.edit(params, clientId)
    }

    override fun delete(clientId: KUUID) {
        action.delete(clientId)
    }

    override fun delta(accountId: KUUID) {
        action.delta(accountId)
    }

    @PostConstruct
    override fun onConstruct() {
        action.onConstruct()
    }
}
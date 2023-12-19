package io.github.driveindex.dto.req.user

import io.github.driveindex.client.ClientType
import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ClientCreateReqDto(
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: ClientType,
    @SerialName("data")
    val data: JsonObject,
)

@Serializable
data class ClientEditReqDto(
    @SerialName("client_id")
    val clientId: KUUID,
    @SerialName("client_type")
    val clientType: ClientType,
    @SerialName("data")
    val data: JsonObject,
)

@Serializable
data class ClientDeleteReqDto(
    @SerialName("client_id")
    val clientId: KUUID,
)

package io.github.driveindex.dto.req.user

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientType
import java.util.*

data class ClientCreateReqDto(
    val name: String,
    val type: ClientType,
    val data: JsonObject,
)

data class ClientEditReqDto(
    val clientId: UUID,
    val type: ClientType,
    val data: JsonObject,
)

data class ClientLoginReqDto(
    val clientId: UUID,
    val redirectUri: String,
)

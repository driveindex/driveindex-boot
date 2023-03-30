package io.github.driveindex.dto.req.user

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientType
import java.util.UUID

data class ClientEditReqDto(
    val clientId: UUID,
    val type: ClientType,
    val data: JsonObject,
)

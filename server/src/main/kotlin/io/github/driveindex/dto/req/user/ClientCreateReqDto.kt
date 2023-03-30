package io.github.driveindex.dto.req.user

import com.google.gson.JsonObject
import io.github.driveindex.client.ClientType

data class ClientCreateReqDto(
    val name: String,
    val type: ClientType,
    val data: JsonObject,
)

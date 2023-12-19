package io.github.driveindex.dto.req.user

import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CommonSettingsReqDto(
    @SerialName("nick")
    val nick: String? = null,
    @SerialName("cors_origin")
    val corsOrigin: String? = null,
)

@Serializable
data class AccountDeleteReqDto(
    @SerialName("account_id")
    val accountId: KUUID
)

@Serializable
data class ClientListReqDto(
    @SerialName("client_id")
    val clientId: KUUID
)

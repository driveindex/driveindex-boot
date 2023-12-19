package io.github.driveindex.dto.resp

import io.github.driveindex.core.util.KUUID
import io.github.driveindex.security.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonSettingsRespDto(
    @SerialName("nick")
    val nick: String,
    @SerialName("cors_origin")
    val corsOrigin: String,
): RespResultData

@Serializable
data class CommonSettingsUserItemRespDto(
    @SerialName("id")
    val id: KUUID,
    @SerialName("username")
    val username: String,
    @SerialName("nick")
    val nick: String,
    @SerialName("role")
    val role: UserRole,
    @SerialName("enable")
    val enable: Boolean,
    @SerialName("cors_origin")
    val corsOrigin: String,
): RespResultData

@Serializable
data class FullSettingsRespDto(
    @SerialName("id")
    val id: KUUID,
    @SerialName("username")
    val username: String? = null,
    @SerialName("password")
    val password: String? = null,
    @SerialName("nick")
    val nick: String? = null,
    @SerialName("cors_origin")
    val corsOrigin: String? = null,
    @SerialName("role")
    val role: UserRole? = null,
    @SerialName("enable")
    val enable: Boolean? = null,
): RespResultData

package io.github.driveindex.dto.req.admin

import io.github.driveindex.core.util.KUUID
import io.github.driveindex.security.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author sgpublic
 * @Date 8/5/23 12:07 PM
 */

@Serializable
data class UserCreateRequestDto(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    @SerialName("nick")
    val nick: String = "",
    @SerialName("role")
    val role: UserRole = UserRole.USER,
    @SerialName("enable")
    val enable: Boolean = true,
)

@Serializable
data class UserDeleteRequestDto(
    @SerialName("user_id")
    val userId: KUUID,
)
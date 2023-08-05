package io.github.driveindex.dto.req.admin

import io.github.driveindex.security.UserRole
import kotlinx.serialization.Serializable

/**
 * @author sgpublic
 * @Date 8/5/23 12:07 PM
 */

@Serializable
data class UserCreateRequestDto(
    val username: String,
    val nick: String = "",
    val role: UserRole = UserRole.USER,
    val password: String,
)

@Serializable
data class UserDeleteRequestDto(
    val username: String
)
package io.github.driveindex.dto.resp

import io.github.driveindex.security.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author sgpublic
 * @Date 2023/2/8 10:12
 */
@Serializable
data class LoginRespDto(
    @field:Schema(description = "用户名")
    @SerialName("username")
    val username: String,

    @field:Schema(description = "用户昵称")
    @SerialName("nick")
    val nick: String,

    @field:Schema(description = "认证相关信息")
    @SerialName("auth")
    val auth: Auth,
): RespResultData {
    @Serializable
    data class Auth (
        @field:Schema(example = "a5c2bca1aaz3...")
        @SerialName("token")
        val token: String,

        @field:Schema(description = "用户角色", example = "ADMIN")
        @SerialName("role")
        val role: UserRole,
    )
}
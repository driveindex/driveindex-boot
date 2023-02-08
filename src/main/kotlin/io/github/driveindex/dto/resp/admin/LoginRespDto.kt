package io.github.driveindex.dto.resp.admin

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/**
 * @author sgpublic
 * @Date 2023/2/8 10:12
 */
class LoginRespDto(
    @field:Schema(description = "用户登陆凭证，使用请附加到 header 中，key 为 Authentication", example = "a5c2bca1aaz3...")
    private val token: String
): Serializable
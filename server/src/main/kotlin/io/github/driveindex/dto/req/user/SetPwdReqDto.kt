package io.github.driveindex.dto.req.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SetPwdReqDto(
    @SerialName("old_pwd")
    val oldPwd: String,
    @SerialName("new_pwd")
    val newPwd: String,
)
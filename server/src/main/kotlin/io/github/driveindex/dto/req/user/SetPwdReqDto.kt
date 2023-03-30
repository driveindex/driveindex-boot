package io.github.driveindex.dto.req.user

data class SetPwdReqDto(
    val oldPwd: String,
    val newPwd: String,
)
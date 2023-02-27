package io.github.driveindex.dto.req.user

import java.io.Serializable

data class SetCommonReqDto(
    val deltaTick: Int,
    val corsOrigin: String
): Serializable
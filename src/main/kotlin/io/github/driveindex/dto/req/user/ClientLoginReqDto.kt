package io.github.driveindex.dto.req.user

import java.util.UUID

data class ClientLoginReqDto(
    val clientId: UUID,
    val redirectUri: String,
)

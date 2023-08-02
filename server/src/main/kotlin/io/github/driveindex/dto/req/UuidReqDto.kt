package io.github.driveindex.dto.req

import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.Serializable

@Serializable
data class UuidReqDto(
    val id: KUUID
)

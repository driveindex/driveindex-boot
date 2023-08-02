package io.github.driveindex.dto.req.user

import kotlinx.serialization.Serializable


@Serializable
data class GetCommonReqDto(
    val filter: Set<ConfItem>? = null
) {
    enum class ConfItem {
        DeltaTrack, CorsOrigin
    }
}

data class SetCommonReqDto(
    val deltaTick: Int,
    val corsOrigin: String
)
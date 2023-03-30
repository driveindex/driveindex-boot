package io.github.driveindex.dto.req.user

import java.io.Serializable

data class GetCommonReqDto(
    val filter: Set<ConfItem>? = null
): Serializable {
    enum class ConfItem {
        DeltaTrack, CorsOrigin
    }
}

data class SetCommonReqDto(
    val deltaTick: Int,
    val corsOrigin: String
): Serializable
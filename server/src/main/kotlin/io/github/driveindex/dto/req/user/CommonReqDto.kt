package io.github.driveindex.dto.req.user

import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.Serializable


@Serializable
data class CommonSettingsReqDto(
    val nick: String? = null,
    val corsOrigin: String? = null,
)

@Serializable
data class GetCommonReqDto(
    val filter: Set<ConfItem>? = null
) {
    enum class ConfItem {
        DeltaTrack, CorsOrigin
    }
}

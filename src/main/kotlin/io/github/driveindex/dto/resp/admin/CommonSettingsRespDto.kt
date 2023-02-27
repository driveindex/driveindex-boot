package io.github.driveindex.dto.resp.admin

import java.io.Serializable

data class CommonSettingsRespDto(
    var deltaTick: Int? = null,
    var corsOrigin: String? = null,
): Serializable

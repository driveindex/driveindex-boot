package io.github.driveindex.dto.common.admin

import java.io.Serializable

data class CommonSettings(
        val deltaTick: Int,
        val corsOrigin: String
): Serializable

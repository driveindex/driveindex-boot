package io.github.driveindex.dto.feign

import java.io.Serializable

data class AzureFailedResultDto(
    val error: String,
    val errorDescription: String,
    val errorCodes: List<Int>,
    val timestamp: String,
    val traceId: String,
    val correlationId: String,
) : Serializable

data class AzurePortalDtoV1_Token(
    val tokenType: String,
    private val expiresIn: Long,
    val scope: String,
    val accessToken: String,
    val refreshToken: String,
) {
    val expires: Long get() = System.currentTimeMillis() + expiresIn * 1000

    val tokenStr: String get() = "$tokenType $accessToken"
}

data class AzureGraphDtoV2_Me(
    val displayName: String,
    val id: String,
    val userPrincipalName: String,
)
package io.github.driveindex.dto.feign

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.regex.Pattern

@Serializable
data class AzureFailedResultDto(
    val error: String,
    val errorDescription: String,
    val errorCodes: List<Int>,
    val timestamp: String,
    val traceId: String,
    val correlationId: String,
)

@Serializable
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

@Serializable
data class AzureGraphDtoV2_Me(
    val displayName: String,
    val id: String,
    val userPrincipalName: String,
)

@Serializable
data class AzureGraphDtoV2_Me_Drive_Root_Delta(
    @SerialName("@odata.nextLink")
    private val nextLink: String?,
    @SerialName("@odata.deltaLink")
    private val deltaLink: String?,
    val value: List<Value>
) {
    val nextToken: String get() = nextLink?.getToken() ?: ""
    val deltaToken: String? get() = deltaLink?.getToken()

    companion object {
        private val TokenPattern: Pattern = "token=(.*?)".toPattern()
    }
    private fun String.getToken() = TokenPattern.matcher(this).group().substring(6)

    @Serializable
    data class Value(
        val id: String,
        val name: String,
        val size: Long,
        val webUrl: String,
        val parentReference: ParentReference,
        val folder: Unit?,
        val file: File?,
    ) {
        @Serializable
        data class ParentReference(
            val id: String
        )
        @Serializable
        data class File(
            val mimeType: String,
            val hashes: Hashes
        ) {
            @Serializable
            data class Hashes(
                val quickXorHash: String,
                val sha1Hash: String?,
                val sha256Hash: String?,
            )
        }
    }
}
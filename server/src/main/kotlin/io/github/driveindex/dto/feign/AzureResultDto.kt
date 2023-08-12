package io.github.driveindex.dto.feign

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.regex.Pattern

@Serializable
data class AzureFailedResultDto(
    @SerialName("error")
    val error: String,
    @SerialName("errorDescription")
    val errorDescription: String,
    @SerialName("errorCodes")
    val errorCodes: List<Int>,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("traceId")
    val traceId: String,
    @SerialName("correlationId")
    val correlationId: String,
)

@Serializable
data class AzurePortalDtoV1_Token(
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    private val expiresIn: Long,
    @SerialName("scope")
    val scope: String,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
) {
    val expires: Long get() = System.currentTimeMillis() + expiresIn * 1000

    val tokenStr: String get() = "$tokenType $accessToken"
}

@Serializable
data class AzureGraphDtoV2_Me(
    @SerialName("displayName")
    val displayName: String,
    @SerialName("id")
    val id: String,
    @SerialName("userPrincipalName")
    val userPrincipalName: String,
)

@Serializable
data class AzureGraphDtoV2_Me_Drive_Root_Delta(
    @SerialName("@odata.nextLink")
    private val nextLink: String?,
    @SerialName("@odata.deltaLink")
    private val deltaLink: String?,
    @SerialName("value")
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
        @SerialName("id")
        val id: String,
        @SerialName("name")
        val name: String,
        @SerialName("size:")
        val size: Long,
        @SerialName("webUrl")
        val webUrl: String,
        @SerialName("parentReference")
        val parentReference: ParentReference,
        @SerialName("folder")
        val folder: Unit?,
        @SerialName("file")
        val file: File?,
    ) {
        @Serializable
        data class ParentReference(
            @SerialName("id")
            val id: String
        )
        @Serializable
        data class File(
            @SerialName("mimeType")
            val mimeType: String,
            @SerialName("hashes")
            val hashes: Hashes
        ) {
            @Serializable
            data class Hashes(
                @SerialName("quickXorHash")
                val quickXorHash: String,
                @SerialName("sha1Hash")
                val sha1Hash: String?,
                @SerialName("sha256Hash")
                val sha256Hash: String?,
            )
        }
    }
}
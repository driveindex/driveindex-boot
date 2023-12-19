package io.github.driveindex.dto.resp

import io.github.driveindex.core.util.JsonGlobal
import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
data class AccountsDto<T: AccountsDto.Detail>(
    @SerialName("id")
    val id: KUUID,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("user_principal_name")
    val userPrincipalName: String,
    @SerialName("create_at")
    val createAt: Long,
    @SerialName("modify_at")
    val modifyAt: Long?,
    @SerialName("detail")
    val detail: JsonElement,
): RespResultData {
    constructor(
        id: KUUID,
        displayName: String,
        userPrincipalName: String,
        createAt: Long,
        modifyAt: Long?,
        detail: T
    ): this(
        id, displayName, userPrincipalName, createAt, modifyAt,
        JsonGlobal.encodeToJsonElement(Detail.serializer(), detail),
    )

    @Serializable
    sealed interface Detail

    @Serializable
    data class OneDriveAccountDetail(
            @SerialName("azure_user_id")
            val azureUserId: String,
    ): Detail
}

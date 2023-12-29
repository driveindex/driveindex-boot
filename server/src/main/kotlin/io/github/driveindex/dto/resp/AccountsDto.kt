package io.github.driveindex.dto.resp

import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AccountsDto(
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
    val detail: Detail,
): RespResultData {
    @Serializable
    sealed interface Detail

    @Serializable
    data class OneDriveAccountDetail(
            @SerialName("azure_user_id")
            val azureUserId: String,
    ): Detail
}

@Serializable
data class AccountCreateRespDto(
    @SerialName("redirect_url")
    val redirectUrl: String,
): RespResultData

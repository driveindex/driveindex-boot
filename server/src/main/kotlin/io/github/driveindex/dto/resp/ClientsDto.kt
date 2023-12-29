package io.github.driveindex.dto.resp

import io.github.driveindex.client.ClientType
import io.github.driveindex.core.util.KUUID
import io.github.driveindex.database.entity.onedrive.OneDriveClientEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClientsDto(
    @SerialName("id")
    val id: KUUID,
    @SerialName("name")
    var name: String,
    @SerialName("type")
    val type: ClientType,
    @SerialName("create_at")
    val createAt: Long,
    @SerialName("modify_at")
    val modifyAt: Long?,
    @SerialName("detail")
    val detail: Detail,
): RespResultData {
    @Serializable
    sealed interface Detail: RespResultData

    @Serializable
    data class OneDriveClientDetail(
            @SerialName("client_id")
            val clientId: String,
            @SerialName("tenant_id")
            val tenantId: String,
            @SerialName("end_point")
            val endPoint: OneDriveClientEntity.EndPoint,
    ): Detail
}

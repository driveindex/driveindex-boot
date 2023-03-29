package io.github.driveindex.dto.resp.user

import io.github.driveindex.client.ClientType
import io.github.driveindex.h2.entity.OneDriveClientEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

data class ClientsDto<T: Any>(
    val id: UUID,
    var name: String,
    val type: ClientType,
    val createAt: Long,
    val modifyAt: Long?,
    val detail: T,
)

data class OneDriveClientDetail(
    val clientId: String,
    val tenantId: String,
    val endPoint: OneDriveClientEntity.EndPoint,
)
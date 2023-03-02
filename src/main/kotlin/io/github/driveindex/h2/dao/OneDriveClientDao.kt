package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.OneDriveClientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OneDriveClientDao: JpaRepository<OneDriveClientEntity, UUID> {
    @Query("from OneDriveClientEntity where id=:id")
    fun getOneDriveClient(id: UUID): OneDriveClientEntity

    @Query("from OneDriveClientEntity where clientId=:azureClientId and clientSecret=:azureClientSecret and endPoint=:endPoint and tenantId=:tenantId")
    fun findClient(
        azureClientId: String, azureClientSecret: String,
        endPoint: OneDriveClientEntity.EndPoint, tenantId: String,
    ): OneDriveClientEntity?
}
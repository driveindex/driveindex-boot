package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.OneDriveAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OneDriveAccountDao: JpaRepository<OneDriveAccountEntity, UUID> {
    @Query("from OneDriveAccountEntity where azureId=:azureId")
    fun findByAzureId(azureId: String): OneDriveAccountEntity?
}
package io.github.driveindex.h2.dao.onedrive

import io.github.driveindex.h2.entity.onedrive.OneDriveAccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OneDriveAccountDao: JpaRepository<OneDriveAccountEntity, UUID> {
    @Query("from OneDriveAccountEntity where id in :ids and azureUserId=:azureId")
    fun findByAzureId(ids: List<UUID>, azureId: String): OneDriveAccountEntity?

    @Query("update OneDriveAccountEntity set deltaToken=:token where id=:id")
    fun setDeltaTokenById(id: UUID, token: String?)
}
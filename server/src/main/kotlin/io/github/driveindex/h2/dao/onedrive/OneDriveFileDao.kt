package io.github.driveindex.h2.dao.onedrive

import io.github.driveindex.h2.entity.onedrive.OneDriveFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:57
 */
@Repository
interface OneDriveFileDao: JpaRepository<OneDriveFileEntity, UUID> {
    @Query("from OneDriveFileEntity where fileId=:id")
    fun findByParentReference(id: String): OneDriveFileEntity?
}
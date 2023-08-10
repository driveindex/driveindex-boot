package io.github.driveindex.database.dao.onedrive

import io.github.driveindex.database.entity.onedrive.OneDriveFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:57
 */
@Repository
interface OneDriveFileDao: JpaRepository<OneDriveFileEntity, UUID> {
    @Query("from OneDriveFileEntity where id=:id")
    fun getFile(id: UUID): OneDriveFileEntity?
    @Query("from OneDriveFileEntity where fileId=:id")
    fun findByParentReference(id: String): OneDriveFileEntity?
}
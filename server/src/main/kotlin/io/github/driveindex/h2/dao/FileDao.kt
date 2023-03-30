package io.github.driveindex.h2.dao

import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:54
 */
@Repository
interface FileDao: JpaRepository<FileEntity, UUID> {
    @Query("update from FileEntity set name=:name where id=:id")
    fun rename(id: UUID, name: String)
}

fun FileDao.findDirByIdOrNull(id: UUID): FileEntity? {
    return findByIdOrNull(id)?.takeIf { it.isDir }
}

fun FileDao.findMyLocalDirByIdAssert(id: UUID, user: UUID): FileEntity {
    val target = findDirByIdOrNull(id)
        ?: throw FailedResult.Dir.TargetNotFound
    if (target.isRoot) {
        throw FailedResult.Dir.ModifyRoot
    }
    if (!target.isLocal) {
        throw FailedResult.Dir.ModifyRemote
    }
    if (target.createBy != user) {
        throw FailedResult.Dir.TargetNotFound
    }
    return target
}
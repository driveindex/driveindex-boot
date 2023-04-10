package io.github.driveindex.h2.dao

import io.github.driveindex.core.util.CanonicalPath
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:54
 */
@Repository
interface FileDao: JpaRepository<FileEntity, UUID> {
    @Modifying
    @Query("update FileEntity set name=:name where id=:id")
    fun rename(id: UUID, name: String)

    @Query("from FileEntity where path=:path and createBy=:createBy")
    fun findVirtualByPath(path: CanonicalPath, createBy: UUID): FileEntity?

    @Query("from FileEntity where path=:path and accountId=:account")
    fun findLinkedByPath(path: CanonicalPath, account: UUID): FileEntity?

    @Query("from FileEntity where parentId=:parent")
    fun findByParent(parent: UUID): List<FileEntity>
}

fun FileDao.getLocalVirtualDir(path: CanonicalPath, createBy: UUID): FileEntity {
    val target = findTopVirtualDir(path, createBy)
    if (path != target.path) {
        throw FailedResult.Dir.ModifyRemote
    }
    return target
}

fun FileDao.findTopVirtualDir(path: CanonicalPath, createBy: UUID): FileEntity {
    return findVirtualDirIntern(path, createBy)
        ?: throw FailedResult.Dir.TargetNotFound
}

private fun FileDao.findVirtualDirIntern(path: CanonicalPath, createBy: UUID, index: Int = 0): FileEntity? {
    if (index > path.length) {
        return null
    }
    findVirtualByPath(path, createBy)
        ?.takeIf { it.linkTarget != null }
        ?.let { return it }
    (index + 1).let {
        return findVirtualDirIntern(path.subPath(it), createBy, it)
    }
}

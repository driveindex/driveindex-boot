package io.github.driveindex.h2.dao

import io.github.driveindex.core.util.CanonicalPath
import io.github.driveindex.exception.FailedResult
import io.github.driveindex.h2.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    @Query("from FileEntity where path=:path")
    fun findByPath(path: CanonicalPath): FileEntity?

    @Query("from FileEntity where parentId=:parent")
    fun listByParent(parent: UUID): List<FileEntity>
}

fun FileDao.getLocalVirtualDir(path: CanonicalPath): FileEntity {
    val target = findVirtualDir(path)
    if (path != target.path) {
        throw FailedResult.Dir.ModifyRemote
    }
    return target
}

fun FileDao.findVirtualDir(path: CanonicalPath): FileEntity {
    return findVirtualDirIntern(path)
        ?: throw FailedResult.Dir.TargetNotFound
}

private fun FileDao.findVirtualDirIntern(path: CanonicalPath, index: Int = 0): FileEntity? {
    if (index > path.length) {
        return null
    }
    findByPath(path)
        ?.takeIf { it.linkTarget == null }
        ?: return null
    (index + 1).let {
        return findVirtualDirIntern(path.subPath(it), it)
    }
}

package io.github.driveindex.h2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:54
 */
@Entity
@Table(name = "files")
data class FileEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "account_id")
    val accountId: UUID?,

    @Column(name = "create_by")
    val createBy: UUID?,

    @Column(name = "create_at")
    val createAt: Long = System.currentTimeMillis(),

    @Column(name = "modify_at")
    val modifyAt: Long = System.currentTimeMillis(),

    @Column(name = "name")
    var name: String,

    @Column(name = "parent_id")
    val parentId: UUID?,

    @Column(name = "path_hash")
    val pathHash: String,

    @Column(name = "mime_type")
    val mimeType: String,

    @Column(name = "size")
    var size: Long = 0,
) {
    val isDir: Boolean get() = mimeType.endsWith("directory")
    val isLink: Boolean get() = mimeType.startsWith("link")

    val isLocal: Boolean get() = createBy != null
    val isRemote: Boolean get() = accountId != null

    val isRoot: Boolean get() = parentId == null

    companion object {
        private const val LINK = "link"
        private const val DIR = "directory"

        const val TYPE_DIR = "remote/$DIR"
        const val TYPE_LOCAL_DIR = "local/$DIR"
        const val TYPE_LINK_DIR = "$LINK/$DIR"
        const val TYPE_LINK_FILE = "$LINK/file"

        const val CONST_ROOT = "root"
    }
}
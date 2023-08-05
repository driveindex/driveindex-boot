package io.github.driveindex.database.entity

import io.github.driveindex.client.ClientType
import io.github.driveindex.core.util.CanonicalPath
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
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
    @JdbcTypeCode(SqlTypes.VARCHAR)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "account_id")
    val accountId: UUID?,

    @Column(name = "client_type")
    val clientType: ClientType?,

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

    @Column(name = "path")
    val path: CanonicalPath,

    @Column(name = "is_dir")
    val isDir: Boolean,

    @Column(name = "link_target")
    val linkTarget: UUID? = null,

    @Column(name = "size")
    var size: Long = 0,
)
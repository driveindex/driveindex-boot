package io.github.driveindex.h2.entity.onedrive

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:56
 */
@Entity
@Table(name = "file_onedrive")
data class OneDriveFileEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "azure_account_id")
    val accountId: String,

    @Column(name = "file_id")
    val fileId: String,

    @Column(name = "web_url")
    val webUrl: String,

    @Column(name = "mime_type")
    val mimeType: String,

    @Column(name = "quick_xor_hash")
    val quickXorHash: String? = null,

    @Column(name = "sha1_hash")
    val sha1Hash: String? = null,

    @Column(name = "sha256_hash")
    val sha256Hash: String? = null,
)
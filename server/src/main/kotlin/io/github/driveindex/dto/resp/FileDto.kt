package io.github.driveindex.dto.resp

import io.github.driveindex.client.ClientType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author sgpublic
 * @Date 2023/4/10 下午3:14
 */
@Serializable
data class FileListRespDto(
        @SerialName("content_size")
    val contentSize: Int,
        @SerialName("content")
    val content: List<Item<*>>,
): RespResultData {
    @Serializable
    data class Item<T: Item.Detail>(
        @SerialName("name")
        val name: String,
        @SerialName("create_at")
        val createAt: Long,
        @SerialName("modify_at")
        val modifyAt: Long,
        @SerialName("is_dir")
        val isDir: Boolean,
        @SerialName("is_link")
        val isLink: Boolean,
        @SerialName("type")
        val type: ClientType?,
        @SerialName("detail")
        val detail: T,
    ) {
        @Serializable
        sealed interface Detail

        @Serializable
        data class OneDrive(
            @SerialName("web_url")
            val webUrl: String,
            @SerialName("mime_type")
            val mimeType: String,
            @SerialName("quick_xor_hash")
            val quickXorHash: String? = null,
            @SerialName("sha1_hash")
            val sha1Hash: String? = null,
            @SerialName("sha256_hash")
            val sha256Hash: String? = null,
        ): Detail
    }
}


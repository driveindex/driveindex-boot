package io.github.driveindex.dto.req.user

import io.github.driveindex.core.util.CanonicalPath
import io.github.driveindex.core.util.KUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/30 下午3:11
 */

@Serializable
data class CreateDirReqDto(
    @SerialName("name")
    val name: String,
    @SerialName("parent")
    val parent: CanonicalPath,
)

@Serializable
data class CreateLinkReqDto(
    @SerialName("name")
    val name: String?,
    @SerialName("target")
    val target: KUUID,
    @SerialName("parent")
    val parent: CanonicalPath,
)

enum class GetDirReqSort {
    NAME, SIZE, CREATE_TIME, MODIFIED_TIME;

    override fun toString(): String {
        return super.name.lowercase(Locale.getDefault())
    }
}

@Serializable
data class DeleteDirReqDto(
    @SerialName("path")
    val path: CanonicalPath,
)

@Serializable
data class RenameDirReqDto(
    @SerialName("path")
    val path: CanonicalPath,
    @SerialName("name")
    val name: String,
)

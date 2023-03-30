package io.github.driveindex.dto.req.user

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/30 下午3:11
 */
data class CreateDirReqDto(
    val name: String,
    val parent: UUID,
)

data class GetDirReqDto(
    @field:Schema(description = "目标文件 ID")
    @RequestParam(name = "path")
    val path: String,
    @field:Schema(description = "排序规则", allowableValues = ["name", "size", "create_time", "modified_time"], defaultValue = "name")
    @RequestParam(name = "sort_by", required = false)
    val sortBy: Sort,
    @field:Schema(description = "是否升序", defaultValue = "true")
    @RequestParam(name = "asc", required = false)
    val asc: Boolean,
    @field:Schema(description = "分页大小", defaultValue = "20")
    @RequestParam(name = "page_size", required = false)
    val pageSize: Int,
    @field:Schema(description = "页索引", defaultValue = "0")
    @RequestParam(name = "page_index", required = false)
    val pageIndex: Int
) {
    enum class Sort {
        NAME, SIZE, CREATE_TIME, MODIFIED_TIME;

        override fun toString(): String {
            return super.name.lowercase(Locale.getDefault())
        }
    }
}

data class DeleteDirReqDto(
    val id: UUID,
)

data class RenameDirReqDto(
    val id: UUID,
    val name: String,
)

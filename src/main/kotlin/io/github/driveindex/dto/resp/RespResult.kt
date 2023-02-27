package io.github.driveindex.dto.resp

import java.io.Serializable

/**
 * @author sgpublic
 * @Date 2023/2/7 16:07
 */
data class RespResult<T: Serializable> internal constructor(
    val code: Int = 200,
    val message: String = "success.",
    val data: T? = null
): Serializable {
    companion object {
        val SAMPLE = RespResult<Nothing>()
    }
}

fun <T: Serializable> T.resp(): RespResult<T> {
    return RespResult(data = this)
}
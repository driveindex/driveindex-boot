package io.github.driveindex.exception

import io.github.driveindex.core.util.toGson
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.Serializable
import java.nio.charset.StandardCharsets

/**
 * @author sgpublic
 * @Date 2023/2/8 9:15
 */
class FailedResult private constructor(
    val code: Int,
    override val message: String
): RuntimeException(message) {
    companion object {
        val UNSUPPORTED_REQUEST = FailedResult(-400, "不支持的请求方式")
        val WRONG_PASSWORD = FailedResult(-401, "密码错误")
        val ANONYMOUS_DENIED = FailedResult(-405, "请登陆后再试")
        val ACCESS_DENIED = FailedResult(-406, "非常抱歉，您暂时不能访问")
        val EXPIRED_TOKEN = FailedResult(-402, "无效的 token")
        val NOT_FOUND = FailedResult(-404, "您请求的资源不存在")

        val SERVICE_UNAVAILABLE = FailedResult(-500, "服务不可用")
        val INTERNAL_SERVER_ERROR = FailedResult(-500, "服务器内部错误")
        val SERVER_PROCESSING_ERROR = FailedResult(-500, "请求处理出错")
        val NOT_IMPLEMENTATION_ERROR = FailedResult(-500, "别买炒饭了，头发快掉光了(´╥ω╥`)")
    }
}

fun HttpServletResponse.write(result: Serializable) {
    characterEncoding = StandardCharsets.UTF_8.name()
    addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    writer.use {
        it.write(result.toGson())
        it.flush()
    }
}
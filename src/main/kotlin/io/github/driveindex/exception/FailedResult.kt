package io.github.driveindex.exception

import io.github.driveindex.core.util.toGson
import io.github.driveindex.dto.resp.RespResult
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
        val UnsupportedRequest get() = FailedResult(-400, "不支持的请求方式")
        val AnonymousDenied get() = FailedResult(-405, "请登陆后再试")
        val NotFound get() = FailedResult(-404, "您请求的资源不存在")

        val ServiceUnavailable get() = FailedResult(-500, "服务不可用")
        val InternalServerError get() = FailedResult(-500, "服务器内部错误")
        val ServerProcessingError get() = FailedResult(-500, "请求处理出错")
        val NotImplementationError get() = FailedResult(-500, "别买炒饭了，头发快掉光了(´╥ω╥`)")
    }

    fun resp(): RespResult<Nothing> {
        return RespResult(code, message)
    }

    object CommonSettings {
        val DeltaTrackDuration get() = FailedResult(-10101, "文件同步间隔时间只能为正整数")
    }

    object Auth {
        val AccessDenied get() = FailedResult(-406, "非常抱歉，您暂时不能访问")
        val WrongPassword get() = FailedResult(-401, "用户不存在或密码错误")
        val UserDisabled get() = FailedResult(-402, "您的用户已被禁用，请联系管理员")
        val UserDeleted get() = FailedResult(-402, "您的用户已被删除，请联系管理员")
        val ExpiredToken get() = FailedResult(-402, "无效的 token")
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
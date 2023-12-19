package io.github.driveindex.dto.resp

import io.github.driveindex.core.util.JsonGlobal
import io.github.driveindex.core.util.encodeWithoutClassDiscriminator
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.nio.charset.StandardCharsets

/**
 * @author sgpublic
 * @Date 2023/2/7 16:07
 */
@Serializable
sealed interface RespResult<T: Any> {
    val code: Int
    val message: String
    val data: T? get() = null
}

@Serializable
data class SampleRespResult(
    override val code: Int = 200,
    override val message: String = "success."
): RespResult<Unit>

@Serializable
data class DataRespResult<T: RespResultData>(
    override val code: Int = 200,
    override val message: String = "success.",
    override val data: T?
): RespResult<T>

@Serializable
data class ListRespResult<T: Any>(
    override val code: Int = 200,
    override val message: String = "success.",
    override val data: Collection<T>?
): RespResult<Collection<T>>

@Serializable
sealed interface RespResultData

inline fun <reified T: RespResultData> HttpServletResponse.write(result: T, serializer: KSerializer<T>) {
    characterEncoding = StandardCharsets.UTF_8.name()
    addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    writer.use {
        it.write(JsonGlobal.encodeToString(
            DataRespResult.serializer(serializer),
            DataRespResult(data = result),
        ))
        it.flush()
    }
}

fun ServerHttpResponse.writeJson(json: String) {
    headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
    body.writer().use {
        it.write(json)
        it.flush()
    }
}

@RestControllerAdvice
class JsonRespAdvice: ResponseBodyAdvice<Any> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, returnType: MethodParameter, selectedContentType: MediaType, selectedConverterType: Class<out HttpMessageConverter<*>>, request: ServerHttpRequest, response: ServerHttpResponse): Any? {
        val jsonBody = when (body) {
            null, is Unit -> JsonGlobal.encodeWithoutClassDiscriminator(SampleRespResult())
            is RespResultData -> JsonGlobal.encodeWithoutClassDiscriminator(DataRespResult(data = body))
            is Collection<*> -> JsonGlobal.encodeWithoutClassDiscriminator(ListRespResult(data = body as Collection<out RespResultData>))
            else -> return body
        }
        response.writeJson(jsonBody.toString())
        return null
    }
}

package io.github.driveindex.feigh

import feign.Response
import feign.codec.DecodeException
import feign.codec.ErrorDecoder
import io.github.driveindex.dto.feign.AzureFailedResultDto
import io.github.driveindex.exception.AzureDecodeException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets


/**
 * @author sgpublic
 * @Date 2022/8/14 19:18
 */
@Slf4j
@Component
class AzureErrorDecoder : ErrorDecoder {
    private val defaultDecoder: ErrorDecoder.Default = ErrorDecoder.Default()
    override fun decode(s: String, response: Response): Exception {
        try {
            response.body().asReader(StandardCharsets.UTF_8).use { input ->
                val json = StringBuilder()
                val buffer = CharArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    json.appendRange(buffer, 0, length)
                }
                val dto: JsonObject = Json.decodeFromString<JsonObject>(json.toString())
                val error = dto["error"]
                if (error is JsonPrimitive && error.isString) {
                    val resultDto: AzureFailedResultDto = Json.decodeFromString(json.toString())
                    return AzureDecodeException(
                        response.status(), resultDto.error,
                        resultDto.errorDescription, response.request()
                    )
                }
                error as JsonObject
                return AzureDecodeException(
                    response.status(), error["code"].toString(),
                    error["message"].toString(), response.request()
                )
            }
        } catch (e: RuntimeException) {
            return defaultDecoder.decode(s, response)
        } catch (e: Exception) {
            return DecodeException(response.status(), e.message, response.request(), e)
        }
    }
}
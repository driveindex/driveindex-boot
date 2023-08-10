package io.github.driveindex.core.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
val JsonGlobal = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
    encodeDefaults = true
    explicitNulls = false
    useAlternativeNames = false
}

fun jsonObjectOf(vararg contents: Pair<String, Any>): JsonObject {
    return buildJsonObject {
        for ((key, value) in contents) {
            put(key, when (value) {
                is String -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is Int -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is KUUID -> JsonPrimitive(value.toString())
                else -> throw IllegalArgumentException("不支持的类型")
            })
        }
    }
}

@Configuration
class GsonConfig {
    @Bean
    fun customConverters(): HttpMessageConverters {
        val messageConverters: MutableCollection<HttpMessageConverter<*>> = ArrayList()
        val gsonHttpMessageConverter = KotlinSerializationJsonHttpMessageConverter(JsonGlobal)
        messageConverters.add(gsonHttpMessageConverter)
        return HttpMessageConverters(true, messageConverters)
    }
}

object AnySerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Any")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as JsonEncoder
        val jsonElement = serializeAny(value)
        jsonEncoder.encodeJsonElement(jsonElement)
    }

    private fun serializeAny(value: Any?): JsonElement = when (value) {
        is Map<*, *> -> {
            val mapContents = value.entries.associate { mapEntry ->
                mapEntry.key.toString() to serializeAny(mapEntry.value)
            }
            JsonObject(mapContents)
        }
        is List<*> -> {
            val arrayContents = value.map { listEntry -> serializeAny(listEntry) }
            JsonArray(arrayContents)
        }
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        else -> JsonPrimitive(value.toString())
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return deserializeJsonElement(element)
    }

    private fun deserializeJsonElement(element: JsonElement): Any = when (element) {
        is JsonObject -> {
            element.mapValues { deserializeJsonElement(it.value) }
        }
        is JsonArray -> {
            element.map { deserializeJsonElement(it) }
        }
        is JsonPrimitive -> element.toString()
    }
}

object UuidAdapter: KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

typealias KUUID = @Serializable(UuidAdapter::class) UUID

package io.github.driveindex.core.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.driveindex.dto.resp.RespResult
import io.github.driveindex.exception.FailedResult
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import kotlin.reflect.KClass

private val GSON: Gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .disableHtmlEscaping()
    .create()

@Configuration
class GsonConfig {
    @Bean
    fun customConverters(): HttpMessageConverters {
        val messageConverters: MutableCollection<HttpMessageConverter<*>> = ArrayList()
        val gsonHttpMessageConverter = GsonHttpMessageConverter(GSON)
        messageConverters.add(gsonHttpMessageConverter)
        return HttpMessageConverters(true, messageConverters)
    }
}

fun <T: Any> KClass<T>.fromGson(src: String): T {
    return GSON.fromJson(src, this.java)
        ?: throw GsonException()
}

fun <T: Any> Class<T>.fromGson(src: String): T {
    return GSON.fromJson(src, this)
        ?: throw GsonException()
}


fun Any?.toGson(): String {
    return if (this is FailedResult) {
        GSON.toJson(RespResult<Nothing>(code, message))
    } else {
        GSON.toJson(this)
    }
}

class GsonException(message: String? = null): Exception(message ?: "对象序列化失败")
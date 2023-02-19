package io.github.driveindex.core

import io.github.driveindex.Application
import io.github.driveindex.core.util.log
import org.ini4j.Profile
import org.ini4j.Wini
import org.ini4j.spi.BeanTool
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.reflect.KProperty
import kotlin.system.exitProcess

/**
 * @author sgpublic
 * @Date 2022/8/5 11:41
 */
@Component
class ConfigManager {
    companion object {
        private const val SectionCommon = "common"
        val Port by IniVal.New(SectionCommon, "port", 11411)
        val Debug by IniVal.New(SectionCommon, "debug", false)
        val LogPath by IniVal.New(SectionCommon, "log-dir", "/var/log/driveindex")
        var CorsOrigins by IniVar.New(SectionCommon, "cors-origin", "")
        fun getCorsOrigins(): List<String>? {
            if (CorsOrigins.isBlank()) {
                return null
            }
            return CorsOrigins.replace(" ", "").split(";")
        }
        var DeltaTrackingTick by IniVar.New(SectionCommon, "delta_tracking", 5)

        private const val SectionAdmin = "admin"
        var Password by IniVar.New(
            SectionAdmin, "password", Application.APPLICATION_BASE_NAME_LOWER
        )

        private const val SectionSql = "sql"
        val SqlUsername by IniVal.New(SectionSql, "username", Application.APPLICATION_BASE_NAME_LOWER)
        val SqlPassword by IniVal.New(SectionSql, "password", "")
        val SqlDatabasePath by IniVal.New(SectionSql, "path", "./data")

        private const val SectionJwt = "jwt"
        private val TokenSecurityKey by IniVal.New(
            SectionJwt, "security", Application.APPLICATION_BASE_NAME_LOWER
        )

        fun getTokenSecurityKey(): ByteArray {
            var base = TokenSecurityKey.toByteArray(StandardCharsets.UTF_8)
            if (base.size < 128) {
                val clone = base.clone()
                base = ByteArray(128)
                for (i in 0..127) {
                    base[i] = clone[i % clone.size]
                }
            }
            return Base64.getEncoder().encode(base)
        }

        val TokenExpired by IniVal.New(
            SectionJwt, "security", 3600L
        )




        private var config = File("./config", "driveindex.ini")
        private val ini: Wini = Wini()

        init {
            ini.file = config
        }
    }

    init {
        if (!config.exists()) {
            val parent = config.parentFile
            if ((parent.exists() || !parent.mkdirs()) && !config.createNewFile()) {
                log.warn("配置文件创建失败，将使用默认配置！")
            }
        }
        ini.file = config
    }

    @Value("\${config:./config/driveindex.ini}")
    fun setConfigFile(path: String) {
        config = File(path)
        if (!config.isFile) {
            log.error("配置文件不可用：$path")
            exitProcess(-1)
        }
        val configPath: String = try {
            config.canonicalPath
        } catch (e: IOException) {
            config.path
        }
        log.info("使用配置文件：$configPath")
    }

    class IniVar<TypeT>(section: String, key: String, defVal: TypeT, clazz: Class<TypeT>) :
        IniVal<TypeT>(section, key, defVal, clazz) {
        operator fun setValue(companion: ConfigManager.Companion, property: KProperty<*>, value: TypeT) {
            getSection(section)?.let {
                it[key] = toIni(value)
                ini[section] = it
            }
            ini.store()
        }

        private fun toIni(value: TypeT): String {
            return value.toString()
        }

        companion object {
            @Suppress("FunctionName")
            inline fun <reified TypeT> New(
                section: String, key: String, devVal: TypeT
            ): IniVar<TypeT> {
                return IniVar(section, key, devVal, TypeT::class.java)
            }
        }
    }

    open class IniVal<TypeT>(
        protected val section: String,
        protected val key: String,
        private val devVal: TypeT,
        private val clazz: Class<TypeT>
    ) {
        private fun fromIni(origin: String?): TypeT {
            return BeanTool.getInstance().parse(origin, clazz) ?: devVal
        }

        operator fun getValue(companion: ConfigManager.Companion, property: KProperty<*>): TypeT {
            return try {
                fromIni(getSection(section)?.get(key))
            } catch (e: Exception) {
                devVal
            }
        }

        companion object {
            @Throws(IOException::class)
            @JvmStatic
            protected fun getSection(sectionName: String?): Profile.Section? {
                ini.load()
                val section: Profile.Section? = ini[sectionName]
                if (section == null) {
                    ini.add(sectionName)
                    ini.store()
                }
                return ini[sectionName]
            }

            @Suppress("FunctionName")
            inline fun <reified TypeT> New(
                section: String, key: String, devVal: TypeT
            ): IniVal<TypeT> {
                return IniVal(section, key, devVal, TypeT::class.java)
            }
        }
    }
}

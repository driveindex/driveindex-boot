package io.github.driveindex.core.util

import java.sql.Timestamp
import java.time.Instant

/**
 * @author sgpublic
 * @Date 2022/8/17 9:23
 */
object Timestamps {
    fun from(utc: String): Long {
        return Timestamp.from(Instant.parse(utc)).time
    }
}
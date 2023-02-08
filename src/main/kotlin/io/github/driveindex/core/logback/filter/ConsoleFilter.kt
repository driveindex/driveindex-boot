package io.github.driveindex.core.logback.filter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import io.github.driveindex.Application
import org.springframework.beans.factory.annotation.Value

/**
 * @author sgpublic
 * @Date 2022/8/5 18:38
 */
class ConsoleFilter : Filter<ILoggingEvent>() {
    private var self = Level.INFO
    private var out = Level.WARN

    @Value("\${spring.profiles.active}")
    fun setProfileActive(profile: String) {
        val check = "dev" != profile
        self = if (check) Level.INFO else Level.DEBUG
        out = if (check) Level.WARN else Level.INFO
    }

    override fun decide(event: ILoggingEvent): FilterReply {
        val target = if (event.loggerName.startsWith(Application::class.java.packageName)) self else out
        return if (event.level.isGreaterOrEqual(target)) FilterReply.NEUTRAL else FilterReply.DENY
    }
}
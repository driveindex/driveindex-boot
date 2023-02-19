package io.github.driveindex.module

import io.github.driveindex.Application
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.MD5
import io.github.driveindex.core.util.log
import io.github.driveindex.h2.dao.UserDao
import io.github.driveindex.h2.entity.UserEntity
import io.github.driveindex.security.UserRole
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DBSetupModule(
    private val user: UserDao
) {
    @PostConstruct
    fun setup() {
        if (user.count() <= 0L) {
            val pwd: String = if (ConfigManager.Debug) {
                Application.APPLICATION_BASE_NAME_LOWER
            } else {
                UUID.randomUUID().toString().MD5.uppercase()
            }
            log.info("创建默认管理员账户，用户名：admin，密码：$pwd")
            user.saveAndFlush(UserEntity(
                username = "admin",
                password = pwd,
                role = UserRole.ADMIN
            ))
        }
    }
}

@Component
class ScheduleModule(
    private val user: UserDao
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun cleanDeletedUser() {
        log.debug("清除回收站中的用户")
        user.doRealDeleteUser()
    }
}
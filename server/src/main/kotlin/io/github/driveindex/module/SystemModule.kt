package io.github.driveindex.module

import io.github.driveindex.Application
import io.github.driveindex.core.ConfigManager
import io.github.driveindex.core.util.CanonicalPath
import io.github.driveindex.core.util.MD5_UPPER
import io.github.driveindex.core.util.SHA1
import io.github.driveindex.core.util.log
import io.github.driveindex.h2.dao.FileDao
import io.github.driveindex.h2.dao.UserDao
import io.github.driveindex.h2.entity.FileEntity
import io.github.driveindex.h2.entity.UserEntity
import io.github.driveindex.security.UserRole
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

@Component
class DBSetupModule(
    private val user: UserDao,
    private val file: FileDao,
) {
    @PostConstruct
    @Transactional
    fun setup() {
        if (user.count() <= 0L) {
            val pwd: String = if (ConfigManager.Debug) {
                Application.APPLICATION_BASE_NAME_LOWER
            } else {
                UUID.randomUUID().toString().MD5_UPPER
            }
            log.info("创建默认管理员账户，用户名：admin，密码：$pwd")
            val userEntity = UserEntity(
                username = "admin",
                password = pwd,
                role = UserRole.ADMIN
            )
            user.save(userEntity)
            file.save(FileEntity(
                createBy = userEntity.id,
                accountId = null,

                name = CanonicalPath.ROOT_PATH,
                parentId = null,
                path = CanonicalPath.ROOT,
                isDir = true,
                clientType = null,
            ))
        }
    }
}

@Component
class ScheduleModule(
    private val user: UserDao,
) {
    @PostConstruct
    fun setup() {
        cleanDeletedUser()
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun cleanDeletedUser() {
        log.debug("清除回收站中的用户")
        user.doRealDeleteUser()
    }
}
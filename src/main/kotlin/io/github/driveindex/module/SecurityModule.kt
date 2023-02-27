package io.github.driveindex.module

import io.github.driveindex.h2.dao.UserConfigDao
import io.github.driveindex.h2.dao.UserDao
import io.github.driveindex.h2.entity.UserConfigEntity
import io.github.driveindex.h2.entity.UserEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class Current(
    private val user: UserDao,
    private val config: UserConfigDao,
) {
    var User: UserEntity
        get() {
            return SecurityContextHolder.getContext().authentication.details as UserEntity
        }
        set(value) {
            user.save(value)
        }

    var UserConfig: UserConfigEntity
        get() = User.id.let{
            return@let config.getByUser(it) ?: UserConfigEntity(it)
        }
        set(value) {
            config.save(value)
        }
}
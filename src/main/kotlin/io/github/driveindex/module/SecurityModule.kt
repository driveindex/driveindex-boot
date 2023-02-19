package io.github.driveindex.module

import io.github.driveindex.h2.entity.UserEntity
import org.springframework.security.core.context.SecurityContextHolder

val CurrentUser get(): UserEntity {
    return SecurityContextHolder.getContext().authentication.details as UserEntity
}
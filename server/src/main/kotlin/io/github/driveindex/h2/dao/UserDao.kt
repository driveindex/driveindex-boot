package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserDao: JpaRepository<UserEntity, String> {
    @Query("from UserEntity where deleteTime<0 and username=:username")
    fun getValidUser(username: String): UserEntity?

    @Query("from UserEntity where deleteTime<0")
    fun getAllValidUsers(): List<UserEntity>

    @Query("from UserEntity where deleteTime>=0")
    fun getDeletedUsers(): List<UserEntity>

    @Modifying
    @Transactional
    @Query("delete UserEntity where deleteTime>=0 and deleteTime<:now")
    fun doRealDeleteUser(now: Long = System.currentTimeMillis())
}
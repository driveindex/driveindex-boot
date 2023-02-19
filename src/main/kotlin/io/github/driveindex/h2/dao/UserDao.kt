package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserDao: JpaRepository<UserEntity, String> {
    @Query("from UserEntity where deleteTime<0 and username=:username")
    fun getValidUser(username: String): UserEntity?

    @Query("from UserEntity where deleteTime<0")
    fun getAllValidUsers(): List<UserEntity>

    @Query("from UserEntity where deleteTime>=0")
    fun getDeletedUsers(): List<UserEntity>

    @Query("delete from UserEntity where deleteTime>=0 and deleteTime<:now")
    fun doRealDeleteUser(now: Long = System.currentTimeMillis())

    @Query("delete from UserEntity where id in :ids")
    fun doDeleteUser(ids: List<String>)
}
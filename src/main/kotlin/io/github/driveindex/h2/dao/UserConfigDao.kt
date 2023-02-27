package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.UserConfigEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserConfigDao: JpaRepository<UserConfigEntity, String> {
    @Query("from UserConfigEntity where id=:uuid")
    fun getByUser(uuid: UUID): UserConfigEntity?
}
package io.github.driveindex.h2.entity

import io.github.driveindex.security.UserRole
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "username")
    val username: String,

    @Column(name = "password")
    val password: String,

    @Column(name = "nick")
    val nick: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: UserRole = UserRole.USER,

    @Column(name = "enable")
    val enable: Boolean = true,

    @Column(name = "delete_time")
    private var deleteTime: Long = -1
) {
    companion object {
        private const val DeleteTimeout = 30L * 24 * 3600 * 1000
    }
    fun markDeleted(isDeleted: Boolean) {
        deleteTime = if (isDeleted) {
            System.currentTimeMillis() + DeleteTimeout
        } else {
            -1
        }
    }
    fun isDeleted(): Boolean = deleteTime >= 0
}
package io.github.driveindex.h2.entity

import io.github.driveindex.security.UserRole
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "username")
    var username: String,

    @Column(name = "password")
    var password: String,

    @Column(name = "nick")
    var nick: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: UserRole = UserRole.USER,

    @Column(name = "enable")
    var enable: Boolean = true,

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
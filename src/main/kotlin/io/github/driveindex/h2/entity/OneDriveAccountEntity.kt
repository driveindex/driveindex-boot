package io.github.driveindex.h2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "account_onedrive")
data class OneDriveAccountEntity(
    @Id
    @Column(name = "client_id")
    val id: UUID,

    @Column(name = "token_type")
    val tokenType: String,

    @Column(name = "access_token")
    val accessToken: String,

    @Column(name = "token_expire")
    val tokenExpire: Date,

    @Column(name = "refresh_token")
    val refreshToken: String,
)
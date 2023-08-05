package io.github.driveindex.database.entity.onedrive

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "account_onedrive")
data class OneDriveAccountEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "azure_user_id")
    val azureUserId: String,

    @Column(name = "token_type")
    var tokenType: String,

    @Column(name = "access_token")
    var accessToken: String,

    @Column(name = "token_expire")
    var tokenExpire: Long,

    @Column(name = "refresh_token")
    var refreshToken: String,

    @Column(name = "expired")
    var accountExpired: Boolean = false,

    @Column(name = "delta_token")
    var deltaToken: String? = null,
)
package io.github.driveindex.h2.entity

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
    val id: UUID = UUID.randomUUID(),

    @Column(name = "client_id")
    val parentClientId: UUID,

    @Column(name = "azure_id")
    val azureId: UUID,

    @Column(name = "display_name")
    val displayName: String,

    @Column(name = "user_principal_name")
    val userPrincipalName: String,

    @Column(name = "token_type")
    val tokenType: String,

    @Column(name = "access_token")
    val accessToken: String,

    @Column(name = "token_expire")
    val tokenExpire: Long,

    @Column(name = "refresh_token")
    val refreshToken: String,
)
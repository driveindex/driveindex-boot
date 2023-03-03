package io.github.driveindex.h2.entity

import io.github.driveindex.client.ClientType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "accounts")
data class AccountsEntity(
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "client_id")
    val parentClientId: UUID,

    @Column(name = "display_name")
    var displayName: String,

    @Column(name = "user_principal_name")
    val userPrincipalName: String,

    @Column(name = "create_at")
    val createAt: Long = System.currentTimeMillis(),

    @Column(name = "modify_at")
    var modifyAt: Long? = null,
)
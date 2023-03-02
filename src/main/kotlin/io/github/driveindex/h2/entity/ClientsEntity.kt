package io.github.driveindex.h2.entity

import io.github.driveindex.client.ClientType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "clients")
data class ClientsEntity(
    @Id
    @Column(name = "client_id")
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name")
    var name: String,

    @Column(name = "type")
    val type: ClientType,
)
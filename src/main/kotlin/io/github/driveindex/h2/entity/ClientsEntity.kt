package io.github.driveindex.h2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "clients")
data class ClientsEntity(
    @Id
    @Column(name = "client_id")
    val id: String,


)
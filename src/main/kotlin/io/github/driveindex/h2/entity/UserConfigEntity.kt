package io.github.driveindex.h2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "user_config")
data class UserConfigEntity(
    @Id
    @Column(name = "id")
    val id: UUID,

    @Column(name = "delta_tick")
    var deltaTick: Int = 60,

    @Column(name = "cors_origin")
    var corsOrigin: String = "",
)
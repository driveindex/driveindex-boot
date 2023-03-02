package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.ClientsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClientsDao: JpaRepository<ClientsEntity, UUID> {
    @Query("from ClientsEntity where id=:id")
    fun getClient(id: UUID): ClientsEntity?

    @Query("from ClientsEntity where name=:name")
    fun findClient(name: String): ClientsEntity?
}
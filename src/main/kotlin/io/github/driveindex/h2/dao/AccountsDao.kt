package io.github.driveindex.h2.dao

import io.github.driveindex.h2.entity.AccountsEntity
import io.github.driveindex.h2.entity.ClientsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountsDao: JpaRepository<AccountsEntity, UUID> {
    @Query("from AccountsEntity where id=:id")
    fun getClient(id: UUID): ClientsEntity?

    @Query("from AccountsEntity where parentClientId=:clientId and displayName=:name")
    fun findByName(clientId: UUID, name: String): ClientsEntity?

    @Query("select id from AccountsEntity where parentClientId=:clientId")
    fun findByClient(clientId: UUID): List<UUID>
}
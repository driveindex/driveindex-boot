package io.github.driveindex.module

import io.github.driveindex.core.util.log
import io.github.driveindex.database.dao.*
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Component
import java.util.*

/**
 * @author sgpublic
 * @Date 8/5/23 1:03 PM
 */
@Component
class DeletionModule(
    private val em: EntityManager,

    private val userDao: UserDao,
    private val clientsDao: ClientsDao,
    private val accountsDao: AccountsDao,
    private val fileDao: FileDao,
    private val sharedLinkDao: SharedLinkDao,
) {
    fun doUserDeleteAction(userId: UUID) {
        em.transaction("删除用户失败") {
            doRealUserDeleteAction(userId)
        }
    }

    private fun doRealUserDeleteAction(userId: UUID) {
        userDao.deleteById(userId)
        for (clientsEntity in clientsDao.listByUser(userId)) {
            doRealClientDeleteAction(clientsEntity.id)
        }
    }

    fun doClientDeleteAction(clientId: UUID) {
        em.transaction("删除 client 失败") {
            doRealUserDeleteAction(clientId)
        }
    }

    private fun doRealClientDeleteAction(clientId: UUID) {
        clientsDao.deleteById(clientId)
        for (accountsEntity in accountsDao.listByClient(clientId)) {
            doRealAccountDeleteAction(accountsEntity.id)
        }
    }

    fun doAccountDeleteAction(accountId: UUID) {
        em.transaction("删除账号失败") {
            doRealAccountDeleteAction(accountId)
        }
    }

    private fun doRealAccountDeleteAction(accountId: UUID) {
        accountsDao.deleteById(accountId)
        for (accountsEntity in accountsDao.listByClient(accountId)) {
            for (fileEntity in fileDao.listByAccount(accountsEntity.id)) {
                doRealFileDeleteAction(fileEntity.id)
            }
            for (sharedLinkEntity in sharedLinkDao.listByAccount(accountsEntity.id)) {
                doRealSharedLinkDeleteAction(sharedLinkEntity.id)
            }
        }
    }

    fun doFileDeleteAction(fileId: UUID) {
        em.transaction("删除文件失败") {
            doRealFileDeleteAction(fileId)
        }
    }

    private fun doRealFileDeleteAction(fileId: UUID) {
        fileDao.deleteById(fileId)
    }

    fun doSharedLinkDeleteAction(linkId: UUID) {
        em.transaction("删除分享链接失败") {
            doRealSharedLinkDeleteAction(linkId)
        }
    }

    private fun doRealSharedLinkDeleteAction(linkId: UUID) {
        sharedLinkDao.deleteById(linkId)
    }

    private fun EntityManager.transaction(errorMessage: String, block: () -> Unit) {
        val transaction = transaction
        try {
            transaction.begin()
            block()
            transaction.commit()
        } catch (e: Exception) {
            log.warn(errorMessage, e)
            transaction.rollback()
        }
    }
}

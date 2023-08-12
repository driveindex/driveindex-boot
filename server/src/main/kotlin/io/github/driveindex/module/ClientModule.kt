package io.github.driveindex.module

import io.github.driveindex.core.util.log
import io.github.driveindex.database.dao.AccountsDao
import io.github.driveindex.database.dao.ClientsDao
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Component
class ClientModule(
    private val client: ClientsDao,
    private val account: AccountsDao,
) {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    @PostConstruct
    private fun onSetup() {
        executor.execute {
            Thread.sleep(30_000)
            realSetup()
        }
    }

    private fun realSetup() {
        log.trace("delta track start!")
        for (client in client.listIfSupportDelta()) {
            for (accountId in account.selectIdByClient(client.id)) {
                try {
                    client.type.delta(accountId)
                } catch (e: Exception) {
                    log.error("delta track for account $accountId failed", e)
                }
            }
        }
        log.trace("delta track finish! sleep 5 min...")
        Thread.sleep(5 * 60 * 1000)
        onSetup()
    }
}
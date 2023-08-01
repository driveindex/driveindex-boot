package io.github.driveindex.module

import io.github.driveindex.core.util.log
import io.github.driveindex.h2.dao.AccountsDao
import io.github.driveindex.h2.dao.ClientsDao
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
        executor.execute(this::realSetup)
    }

    private fun realSetup() {
        log.trace("delta track start!")
        for (client in client.listIfSupportDelta()) {
            for (accountId in account.selectIdByClient(client.id)) {
                client.type.delta(accountId)
            }
        }
        log.trace("delta track finish! sleep 5 min...")
        Thread.sleep(5 * 60 * 1000)
        onSetup()
    }
}
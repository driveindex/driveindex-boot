package io.github.driveindex

import io.github.driveindex.core.util.log
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author sgpublic
 * @Date 2023/4/10 下午2:50
 */
class SingleThreadTest {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val obj: Object = Object()
    @Test
    fun test() {
        executor.execute(this::realTest)
        synchronized(obj) {
            obj.wait()
        }
    }

    private fun realTest() {
        println("realTest tick")
        Thread.sleep(10)
        executor.execute(this::realTest)
    }
}
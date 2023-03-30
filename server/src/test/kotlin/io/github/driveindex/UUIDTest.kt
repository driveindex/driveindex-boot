package io.github.driveindex

import org.junit.jupiter.api.Test
import java.util.*

class UUIDTest {
    @Test
    fun uuid() {
        val uuid = UUID.randomUUID()
        println(uuid.toString())
        println(uuid.node())
    }
}
package com.example

import org.junit.Assert.*
import org.junit.Test
import java.security.MessageDigest

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSha256PhoneNumberHashing() {
        val phone = "+1 (555) 234-5678"
        val clean = phone.replace(Regex("[^0-9+]"), "")
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(clean.toByteArray())
        val hash = bytes.take(8).joinToString("") { "%02x".format(it) }

        assertNotNull(hash)
        assertEquals(16, hash.length)
    }

    @Test
    fun testSafetyNumberFormatting() {
        val safetyNumberBlocks = listOf(
            "74820", "19482", "38201", "88402",
            "44019", "29184", "01948", "55102",
            "88301", "49201", "10293", "84920"
        )
        val totalDigits = safetyNumberBlocks.sumOf { it.length }
        assertEquals(60, totalDigits)
        assertEquals(12, safetyNumberBlocks.size)
    }
}

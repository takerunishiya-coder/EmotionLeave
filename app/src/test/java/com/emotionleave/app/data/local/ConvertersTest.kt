package com.emotionleave.app.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun stringListRoundTrip_preservesSensitiveTextWithoutDelimiterRules() {
        val original = listOf("night", "stress, fatigue", "quote \"value\"", "line1\nline2")

        val encoded = converters.stringListToJson(original)
        val decoded = converters.jsonToStringList(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun blankJson_returnsEmptyList() {
        assertEquals(emptyList<String>(), converters.jsonToStringList(""))
    }
}

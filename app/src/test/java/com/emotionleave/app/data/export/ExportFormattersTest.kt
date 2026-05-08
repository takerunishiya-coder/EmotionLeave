package com.emotionleave.app.data.export

import org.junit.Assert.assertEquals
import org.junit.Test

class ExportFormattersTest {
    private val formatter = ExportFormatters()

    @Test
    fun csvEscaped_quotesValuesWithCommasQuotesOrLineBreaks() {
        assertEquals("plain", formatter.run { "plain".csvEscaped() })
        assertEquals("\"hello, world\"", formatter.run { "hello, world".csvEscaped() })
        assertEquals("\"say \"\"hi\"\"\"", formatter.run { "say \"hi\"".csvEscaped() })
        assertEquals("\"line1\nline2\"", formatter.run { "line1\nline2".csvEscaped() })
    }
}

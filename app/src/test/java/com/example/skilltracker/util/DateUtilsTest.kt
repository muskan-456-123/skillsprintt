package com.example.skilltracker.util

import org.junit.Assert.*
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `parseIsoDuration parses hours and minutes correctly`() {
        assertEquals(90, DateUtils.parseIsoDuration("PT1H30M"))
        assertEquals(60, DateUtils.parseIsoDuration("PT1H"))
        assertEquals(45, DateUtils.parseIsoDuration("PT45M"))
        assertEquals(150, DateUtils.parseIsoDuration("PT2H30M"))
    }

    @Test
    fun `parseIsoDuration handles seconds rounding`() {
        assertEquals(61, DateUtils.parseIsoDuration("PT1H0M45S")) // 45s rounds up
        assertEquals(10, DateUtils.parseIsoDuration("PT10M15S")) // 15s does not round up
    }

    @Test
    fun `parseIsoDuration returns 0 for invalid input`() {
        assertEquals(0, DateUtils.parseIsoDuration("invalid"))
        assertEquals(0, DateUtils.parseIsoDuration(""))
    }

    @Test
    fun `formatMinutes formats correctly`() {
        assertEquals("2h 30m", DateUtils.formatMinutes(150))
        assertEquals("1h", DateUtils.formatMinutes(60))
        assertEquals("45m", DateUtils.formatMinutes(45))
        assertEquals("0m", DateUtils.formatMinutes(0))
    }

    @Test
    fun `formatTimestamp produces correct format`() {
        assertEquals("01:30:00", DateUtils.formatTimestamp(90))
        assertEquals("00:45:00", DateUtils.formatTimestamp(45))
        assertEquals("02:00:00", DateUtils.formatTimestamp(120))
    }

    @Test
    fun `today returns non-empty string`() {
        assertTrue(DateUtils.today().isNotBlank())
        assertTrue(DateUtils.today().matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }
}

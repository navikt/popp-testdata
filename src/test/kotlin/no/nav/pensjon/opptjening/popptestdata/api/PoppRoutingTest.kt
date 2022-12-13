package no.nav.pensjon.opptjening.popptestdata.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PoppRoutingTest {

    companion object {
        private const val POPP_URL_Q1 = "UrlQ1"
        private const val POPP_URL_Q2 = "UrlQ2"
    }

    private val poppRouting = PoppRouting(POPP_URL_Q1, POPP_URL_Q2)

    @Test
    fun `Given Q1 as input When get url Then return Q1 url`() {
        assertEquals(POPP_URL_Q1, poppRouting.getUrl(Environment.Q1))
    }

    @Test
    fun `Given Q2 as input When get url Then return Q1 url`() {
        assertEquals(POPP_URL_Q2, poppRouting.getUrl(Environment.Q2))
    }
}
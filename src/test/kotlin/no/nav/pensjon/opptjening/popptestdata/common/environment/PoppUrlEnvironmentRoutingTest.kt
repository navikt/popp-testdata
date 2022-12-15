package no.nav.pensjon.opptjening.popptestdata.common.environment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PoppUrlEnvironmentRoutingTest {

    companion object {
        private const val poppUrlQ1 = "UrlQ1"
        private const val poppUrlQ2 = "UrlQ2"
    }

    private val poppUrlEnvironmentRouting = PoppUrlEnvironmentRouting(poppUrlQ1, poppUrlQ2)

    @Test
    fun `Given Q1 as input When get url Then return Q1 url`() {
        assertEquals(poppUrlQ1, poppUrlEnvironmentRouting.getUrl(Environment.Q1))
    }

    @Test
    fun `Given Q2 as input When get url Then return Q1 url`() {
        assertEquals(poppUrlQ2, poppUrlEnvironmentRouting.getUrl(Environment.Q2))
    }
}
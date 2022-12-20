package no.nav.pensjon.opptjening.popptestdata.grunnbelop


import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GrunnbelopTest {

    @Test
    fun `given ar from 1950 to 2055 when calling getG then return value for all years`() {
        (1950..2055).forEach { ar ->
            assertTrue(Grunnbelop.getG(ar) > 0)
        }
    }
}
package no.nav.pensjon.opptjening.popptestdata.environment

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PoppUrlRouting(
    @Value("\${popp.url.q1}") private val poppQ1Url: String,
    @Value("\${popp.url.q2}") private val poppQ2Url: String,
) {
    internal fun getUrl(miljo: Miljo) = when (miljo) {
        Miljo.q1 -> poppQ1Url
        Miljo.q2 -> poppQ2Url
    }
}
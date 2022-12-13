package no.nav.pensjon.opptjening.popptestdata.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PoppRouting(
    @Value("\${POPP_Q1_URL}") private val poppQ1Url: String,
    @Value("\${POPP_Q2_URL}") private val poppQ2Url: String,
) {
    internal fun getUrl(environment: Environment) = when (environment) {
        Environment.Q1 -> poppQ1Url
        Environment.Q2 -> poppQ2Url
    }
}
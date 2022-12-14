package no.nav.pensjon.opptjening.popptestdata.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PoppRouting(
    @Value("\${popp.url.q1}") private val poppQ1Url: String,
    @Value("\${popp.url.q2}") private val poppQ2Url: String,
) {
    internal fun getUrl(environment: Environment) = when (environment) {
        Environment.Q1 -> poppQ1Url
        Environment.Q2 -> poppQ2Url
    }
}
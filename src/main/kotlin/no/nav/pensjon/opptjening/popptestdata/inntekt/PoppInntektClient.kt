package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.environment.PoppRouting
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PoppInntektClient(private val routing: PoppRouting, private val restTemplate: RestTemplate) {

    internal fun lagreInntekt(lagreInntektRequest: LagreInntektRequest) {
        val poppUrl = routing.getUrl(lagreInntektRequest.environment)
        restTemplate.postForEntity("$poppUrl/inntekt", null, String::class.java)
    }
}
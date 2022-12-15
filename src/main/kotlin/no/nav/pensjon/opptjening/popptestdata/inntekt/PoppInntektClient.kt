package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.environment.PoppRouting
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PoppInntektClient(private val routing: PoppRouting, private val restTemplate: RestTemplate) {

    internal fun lagreInntekt(lagreInntektPoppRequest: LagreInntektPoppRequest, env: Environment) {
        val poppUrl = routing.getUrl(env)
        restTemplate.postForEntity("$poppUrl/inntekt", null, String::class.java)
    }
}
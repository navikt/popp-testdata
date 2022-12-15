package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.common.environment.PoppUrlEnvironmentRouting
import no.nav.pensjon.opptjening.popptestdata.common.token.PoppTokenRouting
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PoppInntektClient(
    private val urlRouting: PoppUrlEnvironmentRouting,
    private val tokenRouting: PoppTokenRouting,
    private val restTemplate: RestTemplate
    ) {

    internal fun lagreInntekt(lagreInntektPoppRequest: LagreInntektPoppRequest, env: Environment) {
        val poppUrl = urlRouting.getUrl(env)
        val token = tokenRouting.getTokenProvider(env)

        restTemplate.postForEntity("$poppUrl/inntekt", null, String::class.java)
    }
}
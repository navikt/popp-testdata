package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.common.environment.PoppUrlEnvironmentRouting
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class PoppInntektClient(
    private val urlRouting: PoppUrlEnvironmentRouting,
    private val restTemplate: RestTemplate
) {

    internal fun lagreInntekt(lagreInntektPoppRequest: LagreInntektPoppRequest, env: Environment) {
        val poppUrl = "${urlRouting.getUrl(env)}/inntekt"

        restTemplate.postForEntity(poppUrl, lagreInntektPoppRequest, String::class.java)
    }
}
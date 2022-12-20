package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.environment.PoppUrlRouting
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.HentSumPiRequest
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.HentSumPiResponse
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.LagreInntektPoppRequest
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class PoppInntektClient(
    private val urlRouting: PoppUrlRouting,
    private val restTemplate: RestTemplate
) {

    internal fun hentSumPi(hentSumPiRequest: HentSumPiRequest, env: Environment): HentSumPiResponse? {
        val sumPiUrl = "${urlRouting.getUrl(env)}/inntekt/sumPi"

        return restTemplate.postForObject(sumPiUrl, hentSumPiRequest, HentSumPiResponse::class.java)
    }

    internal fun lagreInntekt(lagreInntektPoppRequest: LagreInntektPoppRequest, env: Environment) {
        val lagreInntektUrl = "${urlRouting.getUrl(env)}/inntekt"

        restTemplate.postForEntity(lagreInntektUrl, lagreInntektPoppRequest, String::class.java)
    }
}
package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.common.environment.PoppUrlEnvironmentRouting
import no.nav.pensjon.opptjening.popptestdata.common.token.PoppTokenRouting
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
            setBearerAuth(tokenRouting.getTokenProvider(env).getToken())
        }

        val request: HttpEntity<LagreInntektPoppRequest> = HttpEntity(lagreInntektPoppRequest, headers)

        restTemplate.postForEntity(
            "$poppUrl/inntekt",
            request,
            String::class.java
        )
    }
}
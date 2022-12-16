package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import org.springframework.stereotype.Service

@Service
class InntektService(private val poppInntektClient: PoppInntektClient) {

    fun lagreInntekter(request: LagreInntektRequest, environment: Environment) {
        createInntektPoppRequests(request).forEach {
            poppInntektClient.lagreInntekt(it, environment)
        }
    }

    private fun createInntektPoppRequests(request: LagreInntektRequest) =
        (request.fomAar..request.tomAar).map { inntektAr ->
            LagreInntektPoppRequest(
                Inntekt(
                    inntektAr = inntektAr,
                    fnr = request.fnr,
                    belop = request.belop
                )
            )
        }
}
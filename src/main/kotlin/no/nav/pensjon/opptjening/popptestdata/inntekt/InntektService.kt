package no.nav.pensjon.opptjening.popptestdata.inntekt

import org.springframework.stereotype.Service

@Service
class InntektService(private val poppInntektClient: PoppInntektClient) {

    fun lagreInntekter(request: LagreInntektRequest) {
        createInntektPoppRequests(request).forEach {
            poppInntektClient.lagreInntekt(it, request.environment)
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
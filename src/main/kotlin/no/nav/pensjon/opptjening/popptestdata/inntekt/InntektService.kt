package no.nav.pensjon.opptjening.popptestdata.inntekt

import org.springframework.stereotype.Service

@Service
class InntektService(private val poppInntektClient: PoppInntektClient) {

    fun lagreInntekt(request: LagreInntektRequest) {
        val inntektRequester = createInntektPoppRequests(request)

        inntektRequester.forEach { poppInntektClient.lagreInntekt(it, request.environment) }
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
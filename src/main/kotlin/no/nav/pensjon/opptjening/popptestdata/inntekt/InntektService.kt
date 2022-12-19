package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.grunnbelop.Grunnbelop
import org.springframework.stereotype.Service

@Service
class InntektService(
    private val poppInntektClient: PoppInntektClient,

    ) {

    fun lagreInntekter(request: LagreInntektRequest, environment: Environment) {
        val poppInntektRequests = createPoppInntektRequests(request)

        poppInntektRequests.forEach { poppRequest ->
            poppInntektClient.lagreInntekt(poppRequest, environment)
        }
    }

    private fun createPoppInntektRequests(request: LagreInntektRequest): List<LagreInntektPoppRequest> {
        return when (request.redusertMedGrunnbelop) {
            false -> createRequestsWithConstantBelop(request)
            true -> createRequestsWithRedusertMedGrunnbelop(request)
        }
    }

    private fun createRequestsWithConstantBelop(request: LagreInntektRequest): List<LagreInntektPoppRequest> {
        return (request.fomAar..request.tomAar).map { inntektAr ->
            LagreInntektPoppRequest(
                Inntekt(
                    inntektAr = inntektAr,
                    fnr = request.fnr,
                    belop = request.belop
                )
            )
        }
    }

    private fun createRequestsWithRedusertMedGrunnbelop(request: LagreInntektRequest): List<LagreInntektPoppRequest> {
        val amountOfGrunnbelop = request.belop / Grunnbelop.getG(request.tomAar)

        return (request.fomAar..request.tomAar).map { inntektAr ->
            LagreInntektPoppRequest(
                Inntekt(
                    inntektAr = inntektAr,
                    fnr = request.fnr,
                    belop = (amountOfGrunnbelop * Grunnbelop.getG(inntektAr)).toLong()
                )
            )
        }
    }
}
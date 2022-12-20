package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.grunnbelop.Grunnbelop
import org.springframework.stereotype.Service

@Service
class InntektService(private val poppInntektClient: PoppInntektClient) {

    fun hentInntekt(fnr: String, fomAr: Int, environment: Environment): List<Inntekt> {
        return emptyList()
    }

    fun lagreInntekter(request: LagreInntektRequest, environment: Environment) {
        val poppInntektRequests = createPoppInntektRequests(request)

        poppInntektRequests.forEach { poppRequest ->
            poppInntektClient.lagreInntekt(poppRequest, environment)
        }
    }

    private fun createPoppInntektRequests(request: LagreInntektRequest): List<LagreInntektPoppRequest> {
        return when (request.redusertMedGrunnbelop) {
            true -> createRequestsWithRedusertMedGrunnbelop(request)
            false -> createRequestsWithConstantBelop(request)
        }
    }

    private fun createRequestsWithRedusertMedGrunnbelop(request: LagreInntektRequest): List<LagreInntektPoppRequest> {
        val amountOfGrunnbelop = request.belop / Grunnbelop.getG(request.tomAar)

        return (request.fomAar..request.tomAar).map { inntektAr ->
            LagreInntektPoppRequest(
                InntektPopp(
                    inntektAr = inntektAr,
                    fnr = request.fnr,
                    belop = (amountOfGrunnbelop * Grunnbelop.getG(inntektAr)).toLong()
                )
            )
        }
    }

    private fun createRequestsWithConstantBelop(request: LagreInntektRequest) =
        (request.fomAar..request.tomAar).map { inntektAr ->
            LagreInntektPoppRequest(
                InntektPopp(
                    inntektAr = inntektAr,
                    fnr = request.fnr,
                    belop = request.belop
                )
            )
        }
}
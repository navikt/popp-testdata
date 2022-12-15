package no.nav.pensjon.opptjening.popptestdata.inntekt

import org.springframework.stereotype.Service

@Service
class InntektService(private val poppInntektClient: PoppInntektClient) {

    fun lagreInntekt(request: LagreInntektRequest) = poppInntektClient.lagreInntekt(request)
}
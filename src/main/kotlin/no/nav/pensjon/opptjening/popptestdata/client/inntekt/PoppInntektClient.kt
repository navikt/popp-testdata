package no.nav.pensjon.opptjening.popptestdata.client.inntekt

import no.nav.pensjon.opptjening.popptestdata.api.PoppRouting
import no.nav.pensjon.opptjening.popptestdata.api.inntekt.LagreInntektRequest
import org.springframework.stereotype.Component

@Component
class PoppInntektClient(private val routing: PoppRouting) {

    internal fun lagreInntekt(lagreInntektRequest: LagreInntektRequest) {

    }
}
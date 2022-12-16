package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.ENVIRONMENT_HEADER
import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.common.exceptionhandling.requestRequirement
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController


@Protected
@RestController
class InntektController(private val inntektService: InntektService) {
    @PostMapping("/inntekt")
    fun lagreInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = ENVIRONMENT_HEADER, required = true) environment: Environment,
        @RequestBody request: LagreInntektRequest,
    ): ResponseEntity<*> {
        requestRequirement(request.fomAar <= request.tomAar) { "FomAr is grater than tomAr in request. fomAar was ${request.fomAar} and tomAar was ${request.tomAar}" }

        inntektService.lagreInntekter(request, environment)
        return ResponseEntity.ok(HttpStatus.OK)
    }
}
//https://pensjon-regler-q1.dev.adeo.no/api/hentVeietGrunnbelopListe?fomAr=1950&tomAr=2050
package no.nav.pensjon.opptjening.popptestdata.inntekt

import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.common.requestRequirement
import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor.Companion.ENVIRONMENT_HEADER
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


//@Protected
@Unprotected
@RestController
class InntektController(private val inntektService: InntektService) {

    companion object{
        private const val FIRST_FOM_YEAR = 1968
    }
    @PostMapping("/inntekt")
    fun lagreInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = ENVIRONMENT_HEADER, required = true) environment: Environment,
        @RequestBody request: LagreInntektRequest,
    ): ResponseEntity<*> {
        requestRequirement(request.fomAar <= request.tomAar) { "FomAr is grater than tomAr in request. fomAar was ${request.fomAar} and tomAar was ${request.tomAar}" }
        requestRequirement(request.fomAar >= FIRST_FOM_YEAR) { "FomAr is before $FIRST_FOM_YEAR. fomAar was ${request.fomAar}" }
        requestRequirement(request.tomAar <= LocalDate.now().year) { "TomAr is after current year ${LocalDate.now().year}. fomAar was ${request.tomAar}" }

        inntektService.lagreInntekter(request, environment)
        return ResponseEntity.ok(HttpStatus.OK)
    }

    @GetMapping("/inntekt")
    fun hentInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = ENVIRONMENT_HEADER, required = true) environment: Environment,
        @RequestHeader(value = "fnr", required = true) fnr: String
    ): List<Inntekt> {
        return inntektService.hentInntekt(fnr = fnr, fomAr = FIRST_FOM_YEAR, environment)
    }
}
//https://pensjon-regler-q1.dev.adeo.no/api/hentVeietGrunnbelopListe?fomAr=1950&tomAr=2050
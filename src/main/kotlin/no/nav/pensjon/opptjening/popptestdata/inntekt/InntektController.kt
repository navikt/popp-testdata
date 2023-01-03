package no.nav.pensjon.opptjening.popptestdata.inntekt


import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.common.requestRequirement
import no.nav.pensjon.opptjening.popptestdata.environment.Miljo
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.Inntekt
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.LagreInntektRequest
import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor.Companion.MILJO
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@Protected
class InntektController(private val inntektService: InntektService) {

    companion object {
        const val FIRST_FOM_YEAR = 1968
        const val INNTEKT_PATH = "/api/v1/inntekt"
    }

    @PostMapping("/api/v1/inntekt")
    fun lagreInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = MILJO, required = true) miljo: Miljo,
        @RequestBody request: LagreInntektRequest,
    ): ResponseEntity<*> {
        requestRequirement(request.fomAar <= request.tomAar) { "FomAr is grater than tomAr in request. fomAar was ${request.fomAar} and tomAar was ${request.tomAar}" }
        requestRequirement(request.fomAar >= FIRST_FOM_YEAR) { "FomAr is before $FIRST_FOM_YEAR. fomAar was ${request.fomAar}" }
        requestRequirement(request.tomAar <= LocalDate.now().year) { "TomAr is after current year ${LocalDate.now().year}. fomAar was ${request.tomAar}" }

        inntektService.lagreInntekter(request, miljo)
        return ResponseEntity.ok(HttpStatus.OK)
    }

    @GetMapping("/api/v1/inntekt")
    fun hentInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = MILJO, required = true) miljo: Miljo,
        @RequestParam(value = "fnr", required = true) fnr: String
    ): List<Inntekt> {
        return inntektService.hentInntekt(fnr = fnr, fomAr = FIRST_FOM_YEAR, miljo)
    }
}

//TODO Request mapping
//Miljø i egen mappe
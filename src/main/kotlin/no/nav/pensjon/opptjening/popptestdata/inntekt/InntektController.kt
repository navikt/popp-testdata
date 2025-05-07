package no.nav.pensjon.opptjening.popptestdata.inntekt


import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.common.requestRequirement
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.Inntekt
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.LagreInntektRequest
import no.nav.pensjon.opptjening.popptestdata.miljo.Miljo
import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor.Companion.MILJO
import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate


@RestController
@RequestMapping("/api/v1/inntekt")
@Protected
class InntektController(private val inntektService: InntektService) {

    companion object {
        const val FIRST_FOM_YEAR = 1968
        val log = LoggerFactory.getLogger(InntektController::class.java)
    }

    @PostMapping
    fun lagreInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = MILJO, required = true) miljo: Miljo,
        @RequestBody request: LagreInntektRequest,
    ): ResponseEntity<*> {
        requestRequirement(request.fomAar <= request.tomAar) { "FomAr is grater than tomAr in request. fomAar was ${request.fomAar} and tomAar was ${request.tomAar}" }
        requestRequirement(request.fomAar >= FIRST_FOM_YEAR) { "FomAr is before $FIRST_FOM_YEAR. fomAar was ${request.fomAar}" }
        requestRequirement(request.tomAar <= LocalDate.now().year) { "TomAr is after current year ${LocalDate.now().year}. fomAar was ${request.tomAar}" }

        println("Lagrer inntekter for fomAar=${request.fomAar} tomAar=${request.tomAar}")
        log.info("Lagrer inntekter for fomAar=${request.fomAar} tomAar=${request.tomAar}")

        inntektService.lagreInntekter(request, miljo)
        return ResponseEntity.ok(HttpStatus.OK)
    }

    @GetMapping
    fun hentInntekter(
        @RequestHeader(value = NAV_CALL_ID, required = true) callId: String,
        @RequestHeader(value = NAV_CONSUMER_ID, required = true) consumerId: String,
        @RequestHeader(value = MILJO, required = true) miljo: Miljo,
        @RequestParam(value = "fnr", required = true) fnr: String
    ): List<Inntekt> {
        return inntektService.hentInntekt(fnr = fnr, fomAr = FIRST_FOM_YEAR, miljo)
    }
}
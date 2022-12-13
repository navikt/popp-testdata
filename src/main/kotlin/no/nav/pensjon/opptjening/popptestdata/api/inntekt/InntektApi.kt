package no.nav.pensjon.opptjening.popptestdata.api.inntekt

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
class InntektApi {

    //https://pensjon-regler-q1.dev.adeo.no/api/hentVeietGrunnbelopListe?fomAr=1950&tomAr=2050
    @PostMapping("/inntekt")
    fun lagreInntekt(
        @RequestHeader(value = "Nav-Call-Id", required = false) callId: String,
        @RequestHeader(value = "Nav-Consumer-Id", required = false) consumerId: String,
        @RequestBody request: LagreInntektRequest
    ): ResponseEntity<*>? {
        return ResponseEntity.ok(HttpStatus.OK)
    }
}
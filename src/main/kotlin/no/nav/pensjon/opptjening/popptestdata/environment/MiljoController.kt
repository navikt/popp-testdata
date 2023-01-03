package no.nav.pensjon.opptjening.popptestdata.environment

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
class MiljoController {

    @GetMapping("/api/v1/miljo")
    fun availableEnvironments(): ResponseEntity<List<Miljo>> = ResponseEntity.ok(Miljo.values().toList())
}
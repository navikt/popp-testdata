package no.nav.pensjon.opptjening.popptestdata.health

import no.nav.pensjon.opptjening.popptestdata.environment.Miljo
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Unprotected
class HealthController {

    @GetMapping("/api/v1/miljo")
    fun availableEnvironments(): ResponseEntity<List<Miljo>> = ResponseEntity.ok(Miljo.values().toList())

    @GetMapping("/ping")
    fun ping(): ResponseEntity<Unit> = ResponseEntity.ok().build()

    @GetMapping("/internal/isalive")
    fun isalive(): ResponseEntity<String> = ResponseEntity.ok("Is alive")

    @GetMapping("/internal/isready")
    fun isready(): ResponseEntity<String> = ResponseEntity.ok("Is ready")
}
package no.nav.pensjon.opptjening.popptestdata

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableJwtTokenValidation
class PoppTestdataApp

fun main(args: Array<String>) {
    runApplication<PoppTestdataApp>(*args)
}
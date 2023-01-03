package no.nav.pensjon.opptjening.popptestdata.token

import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import pensjon.opptjening.azure.ad.client.TokenProvider

@Component
class PoppToken(
    @Qualifier("poppTokenProviderQ1") private val poppTokenProviderQ1: TokenProvider,
    @Qualifier("poppTokenProviderQ2") private val poppTokenProviderQ2: TokenProvider,
) {
    internal fun getToken(environment: Environment) = when (environment) {
        Environment.q1 -> poppTokenProviderQ1.getToken()
        Environment.q2 -> poppTokenProviderQ2.getToken()
    }
}
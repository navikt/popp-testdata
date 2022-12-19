package no.nav.pensjon.opptjening.popptestdata.token

import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import pensjon.opptjening.azure.ad.client.TokenProvider

@Component
class PoppTokenRouting(
    @Qualifier("azureAdtokenProviderQ1") private val poppTokenProviderQ1: TokenProvider,
    @Qualifier("azureAdtokenProviderQ2") private val poppTokenProviderQ2: TokenProvider,
) {
    internal fun getTokenProvider(environment: Environment) = when (environment) {
        Environment.Q1 -> poppTokenProviderQ1
        Environment.Q2 -> poppTokenProviderQ2
    }
}
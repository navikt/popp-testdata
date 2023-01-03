package no.nav.pensjon.opptjening.popptestdata.token

import no.nav.pensjon.opptjening.popptestdata.environment.Miljo
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import pensjon.opptjening.azure.ad.client.TokenProvider

@Component
class PoppToken(
    @Qualifier("poppTokenProviderQ1") private val poppTokenProviderQ1: TokenProvider,
    @Qualifier("poppTokenProviderQ2") private val poppTokenProviderQ2: TokenProvider,
) {
    internal fun getToken(miljo: Miljo) = when (miljo) {
        Miljo.q1 -> poppTokenProviderQ1.getToken()
        Miljo.q2 -> poppTokenProviderQ2.getToken()
    }
}
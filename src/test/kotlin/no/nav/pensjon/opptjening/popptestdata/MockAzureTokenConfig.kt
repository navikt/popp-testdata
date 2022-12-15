package no.nav.pensjon.opptjening.popptestdata

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider
import pensjon.opptjening.azure.ad.client.mock.MockTokenProvider

@Configuration
class MockAzureTokenConfig {

    @Bean("azureAdtokenProviderQ1")
    fun azureAdTokenProviderQ1(): TokenProvider = MockTokenProvider(POPP_Q1_TOKEN)

    @Bean("azureAdtokenProviderQ2")
    fun azureAdTokenProviderQ2(): TokenProvider = MockTokenProvider(POPP_Q2_TOKEN)

    companion object{
        const val POPP_Q1_TOKEN = "test.q1.Token"
        const val POPP_Q2_TOKEN = "test.q2.Token"
    }
}
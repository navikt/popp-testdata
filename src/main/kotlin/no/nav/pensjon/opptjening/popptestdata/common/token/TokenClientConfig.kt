package no.nav.pensjon.opptjening.popptestdata.common.token

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import pensjon.opptjening.azure.ad.client.AzureAdTokenProvider
import pensjon.opptjening.azure.ad.client.AzureAdVariableConfig
import pensjon.opptjening.azure.ad.client.TokenProvider
import java.net.URL

@Configuration
class TokenClientConfig {

    @Bean("azureAdConfigQ1")
    @Profile("dev-fss", "prod-fss")
    fun azureAdConfigQ1(
        @Value("\${AZURE_APP_CLIENT_ID}") azureAppClientId: String,
        @Value("\${AZURE_APP_CLIENT_SECRET}") azureAppClientSecret: String,
        @Value("\${PGI_ENDRING_API_ID}") pgiEndringApiId: String,
        @Value("\${AZURE_APP_WELL_KNOWN_URL}") wellKnownUrl: String,
        @Value("\${PROXY_URL:null}") proxyUrl: String?,
    ) = AzureAdVariableConfig(
        azureAppClientId = azureAppClientId,
        azureAppClientSecret = azureAppClientSecret,
        targetApiId = pgiEndringApiId,
        wellKnownUrl = wellKnownUrl,
        proxyUrl = proxyUrl?.let { URL(proxyUrl) }
    )

    @Bean("azureAdtokenProviderQ1")
    @Profile("dev-fss", "prod-fss")
    fun azureAdtokenProviderQ1(@Qualifier("azureAdConfigQ1") azureAdVariableConfig: AzureAdVariableConfig): TokenProvider = AzureAdTokenProvider(azureAdVariableConfig)


    @Bean("azureAdConfigQ2")
    @Profile("dev-fss", "prod-fss")
    fun azureAdConfigQ2(
        @Value("\${AZURE_APP_CLIENT_ID}") azureAppClientId: String,
        @Value("\${AZURE_APP_CLIENT_SECRET}") azureAppClientSecret: String,
        @Value("\${PGI_ENDRING_API_ID}") pgiEndringApiId: String,
        @Value("\${AZURE_APP_WELL_KNOWN_URL}") wellKnownUrl: String,
        @Value("\${PROXY_URL:null}") proxyUrl: String?,
    ) = AzureAdVariableConfig(
        azureAppClientId = azureAppClientId,
        azureAppClientSecret = azureAppClientSecret,
        targetApiId = pgiEndringApiId,
        wellKnownUrl = wellKnownUrl,
        proxyUrl = proxyUrl?.let { URL(proxyUrl) }
    )

    @Bean("azureAdtokenProviderQ2")
    @Profile("dev-fss", "prod-fss")
    fun azureAdtokenProviderQ2(@Qualifier("azureAdConfigQ2") azureAdVariableConfig: AzureAdVariableConfig): TokenProvider = AzureAdTokenProvider(azureAdVariableConfig)


}
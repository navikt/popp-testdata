package no.nav.pensjon.opptjening.popptestdata.token

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
class PoppTokenProviderConfig {

    @Bean("poppAzureAdConfigQ1")
    @Profile("dev-gcp")
    fun poppAzureAdConfigQ1(
        @Value("\${AZURE_APP_CLIENT_ID}") azureAppClientId: String,
        @Value("\${AZURE_APP_CLIENT_SECRET}") azureAppClientSecret: String,
        @Value("\${POPP_Q1_API_ID}") pgiEndringApiId: String,
        @Value("\${AZURE_APP_WELL_KNOWN_URL}") wellKnownUrl: String,
        @Value("\${PROXY_URL:null}") proxyUrl: String?,
    ) = AzureAdVariableConfig(
        azureAppClientId = azureAppClientId,
        azureAppClientSecret = azureAppClientSecret,
        targetApiId = pgiEndringApiId,
        wellKnownUrl = wellKnownUrl,
        proxyUrl = proxyUrl?.let { if (proxyUrl == "null") null else URL(proxyUrl) }
    )

    @Bean("poppAzureAdConfigQ2")
    @Profile("dev-gcp")
    fun poppAzureAdConfigQ2(
        @Value("\${AZURE_APP_CLIENT_ID}") azureAppClientId: String,
        @Value("\${AZURE_APP_CLIENT_SECRET}") azureAppClientSecret: String,
        @Value("\${POPP_Q2_API_ID}") pgiEndringApiId: String,
        @Value("\${AZURE_APP_WELL_KNOWN_URL}") wellKnownUrl: String,
        @Value("\${PROXY_URL:null}") proxyUrl: String?,
    ) = AzureAdVariableConfig(
        azureAppClientId = azureAppClientId,
        azureAppClientSecret = azureAppClientSecret,
        targetApiId = pgiEndringApiId,
        wellKnownUrl = wellKnownUrl,
        proxyUrl = proxyUrl?.let { if (proxyUrl == "null") null else URL(proxyUrl) }
    )

    @Bean("poppTokenProviderQ1")
    @Profile("dev-gcp")
    fun poppTokenProviderQ1(@Qualifier("poppAzureAdConfigQ1") azureAdVariableConfig: AzureAdVariableConfig): TokenProvider =
        AzureAdTokenProvider(azureAdVariableConfig)

    @Bean("poppTokenProviderQ2")
    @Profile("dev-gcp")
    fun poppTokenProviderQ2(@Qualifier("poppAzureAdConfigQ2") azureAdVariableConfig: AzureAdVariableConfig): TokenProvider =
        AzureAdTokenProvider(azureAdVariableConfig)
}
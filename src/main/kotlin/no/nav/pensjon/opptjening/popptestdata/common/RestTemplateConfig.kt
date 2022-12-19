package no.nav.pensjon.opptjening.popptestdata.common

import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RestTemplateConfig {
    @Bean
    fun poppTemplate(headerInterceptor: HeaderInterceptor, tokenInterceptor: TokenInterceptor) = RestTemplateBuilder()
        .additionalInterceptors(headerInterceptor, tokenInterceptor)
        .build()
}
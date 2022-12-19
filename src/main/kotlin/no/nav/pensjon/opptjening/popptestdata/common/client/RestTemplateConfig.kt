package no.nav.pensjon.opptjening.popptestdata.common.client

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateConfig {
    @Bean
    fun poppTemplate(headerInterceptor: HeaderInterceptor): RestTemplate = RestTemplateBuilder()
        .additionalInterceptors(headerInterceptor)
        .build()
}
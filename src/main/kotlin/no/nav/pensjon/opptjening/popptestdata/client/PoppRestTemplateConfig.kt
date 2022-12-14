package no.nav.pensjon.opptjening.popptestdata.client

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PoppRestTemplateConfig {
    @Bean
    fun template(): RestTemplate {
        return RestTemplate()
    }
}
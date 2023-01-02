package no.nav.pensjon.opptjening.popptestdata.common

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component


@Component
class Documentation {

    @Bean
    fun customOpenAPI(): OpenAPI? {
        return OpenAPI()
            .components(Components()
                    .addSecuritySchemes("doc-bearer-token", SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                    )
            )
    }
}
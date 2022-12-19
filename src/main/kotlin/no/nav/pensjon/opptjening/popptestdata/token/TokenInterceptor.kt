package no.nav.pensjon.opptjening.popptestdata.token

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

@Component
class TokenInterceptor(private val poppToken: PoppToken) : ClientHttpRequestInterceptor {

    @Autowired
    var controllerRequest: HttpServletRequest? = null

    companion object{
        const val ENVIRONMENT_HEADER = "environment"
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val environment = Environment.valueOf(controllerRequest!!.getHeader(ENVIRONMENT_HEADER).toString())

        request.headers.setBearerAuth(poppToken.getToken(environment))

        return execution.execute(request, body)
    }
}



package no.nav.pensjon.opptjening.popptestdata.token

import jakarta.servlet.http.HttpServletRequest
import no.nav.pensjon.opptjening.popptestdata.environment.Miljo
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
        const val MILJO = "miljo"
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val miljo = Miljo.valueOf(controllerRequest!!.getHeader(MILJO).toString())

        request.headers.setBearerAuth(poppToken.getToken(miljo))

        return execution.execute(request, body)
    }
}



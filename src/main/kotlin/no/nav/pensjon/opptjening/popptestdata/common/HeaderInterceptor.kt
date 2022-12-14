package no.nav.pensjon.opptjening.popptestdata.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component

@Component
class HeaderInterceptor : ClientHttpRequestInterceptor {

    @Autowired
    var controllerRequest: HttpServletRequest? = null

    companion object {
        const val NAV_CALL_ID = "Nav-Call-Id"
        const val NAV_CONSUMER_ID = "Nav-Consumer-Id"
    }

    override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
        val callId = controllerRequest!!.getHeader(NAV_CALL_ID)
        val consumerId = controllerRequest!!.getHeader(NAV_CONSUMER_ID)

        request.headers.apply {
            add(NAV_CALL_ID, callId)
            add(NAV_CONSUMER_ID, consumerId)
            accept = listOf(MediaType.APPLICATION_JSON)
            contentType = MediaType.APPLICATION_JSON
        }

        return execution.execute(request, body)
    }
}
package no.nav.pensjon.opptjening.popptestdata

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.pensjon.opptjening.popptestdata.MockAzureTokenConfig.Companion.POPP_Q1_TOKEN
import no.nav.pensjon.opptjening.popptestdata.MockAzureTokenConfig.Companion.POPP_Q2_TOKEN
import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.ENVIRONMENT_HEADER
import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.client.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.common.model.DEFAULT_CHANGED_BY
import no.nav.pensjon.opptjening.popptestdata.inntekt.*
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(classes = [PoppTestdataApp::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
internal class PoppTestdataAppKtTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var server: MockOAuth2Server

    @BeforeEach
    fun resetWiremock() {
        wiremock.resetAll()
    }

    @Test
    fun `Given a valid lagreInntekt request when calling post inntekt then return 200 ok`() {
        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt()
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1`() {
        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q1)

        wiremock.verify(postRequestedFor(urlEqualToPoppQ1))
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        wiremock.stubFor(post(urlEqualToPoppQ2).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q2)

        wiremock.verify(postRequestedFor(urlEqualToPoppQ2))
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1 with Q1 token`() {
        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q1)

        wiremock.verify(
            postRequestedFor(urlEqualToPoppQ1)
                .withHeader("Authorization", equalTo("Bearer $POPP_Q1_TOKEN"))
        )
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2 with Q2 token`() {
        wiremock.stubFor(post(urlEqualToPoppQ2).willReturn(aResponse().withStatus(200)))

        performPostInntekt(Q2)

        wiremock.verify(
            postRequestedFor(urlEqualToPoppQ2)
                .withHeader("Authorization", equalTo("Bearer $POPP_Q2_TOKEN"))
        )
    }

    @Test
    fun `Given post inntekt with fomAar 2000 and tomAr 2003 then call lagre inntekt in popp four times with inntekt fom 2000 to 2003`() {
        val fomAar = 2000
        val tomAar = 2003

        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(request = inntektRequest(fomAar = fomAar, tomAar = tomAar))

        wiremock.verify((fomAar..tomAar).toList().size, postRequestedFor(urlEqualToPoppQ1))

        val poppRequests = wiremock.findAll(postRequestedFor(urlEqualToPoppQ1))
            .map { it.bodyAsString }
            .map { jacksonObjectMapper().readValue(it, LagreInntektPoppRequest::class.java) }

        (fomAar..tomAar).forEach { inntektAr ->
            assertNotNull(poppRequests.getInntekt(inntektAr)) { " Fant ikke inntektAr: $inntektAr " }
        }
    }

    @Test
    fun `Given post inntekt with fnr 09876543210 ,fomAar 2000, tomAar 2000, belop 20000L then call lagre inntekt in one time with inntekt that contains input`() {
        val request = LagreInntektRequest(
            fnr = "09876543210",
            fomAar = 2000,
            tomAar = 2000,
            belop = 20000L
        )

        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(request = jacksonObjectMapper().writeValueAsString(request))

        wiremock.verify(1, postRequestedFor(urlEqualToPoppQ1))
        val poppRequest = wiremock.findAll(postRequestedFor(urlEqualToPoppQ1))
            .map { it.bodyAsString }
            .map { jacksonObjectMapper().readValue(it, LagreInntektPoppRequest::class.java) }
            .first()

        //Mapped from request
        assertEquals(request.fnr, poppRequest.inntekt.fnr)
        assertEquals(request.belop, poppRequest.inntekt.belop)
        assertEquals(request.fomAar, poppRequest.inntekt.inntektAr)

        //Default values
        assertEquals(DEFAULT_KILDE, poppRequest.inntekt.kilde)
        assertEquals(DEFAULT_KOMMUNE, poppRequest.inntekt.kommune)
        assertEquals(DEFAULT_INNTEKT_TYPE, poppRequest.inntekt.inntektType)
        assertEquals(DEFAULT_CHANGED_BY, poppRequest.inntekt.changeStamp.createdBy)
        assertEquals(DEFAULT_CHANGED_BY, poppRequest.inntekt.changeStamp.updatedBy)
        assertNotNull(poppRequest.inntekt.changeStamp.createdDate)
        assertNotNull(poppRequest.inntekt.changeStamp.updatedDate)
    }

    @Test
    fun `Given post inntekt with redusertMedGrunnbelop true, fomAar 2000 and tomAr 2003 then call lagre inntekt in popp with inntekt redusert with grunnbelop before 2003`() {
        val fomAar = 2000
        val tomAar = 2003
        val belop2003 = 500501L
        val belop2002 = 476076L // 476076,937549138732042
        val belop2001 = 452556L // 452556,145075405636853
        val belop2000 = 432648L // 432648,432510185137127

        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(
            Q1,
            inntektRequest(fomAar = fomAar, tomAar = tomAar, belop = belop2003, redusertMedGrunnbelop = true)
        )

        wiremock.verify((fomAar..tomAar).toList().size, postRequestedFor(urlEqualToPoppQ1))

        val poppRequests: List<LagreInntektPoppRequest> = wiremock.findAll(postRequestedFor(urlEqualToPoppQ1))
            .map { it.bodyAsString }
            .map { jacksonObjectMapper().readValue(it, LagreInntektPoppRequest::class.java) }

        (fomAar..tomAar).forEach { inntektAr ->
            assertNotNull(poppRequests.getInntekt(inntektAr)) { " Fant ikke inntektAr: $inntektAr " }
        }

        assertEquals(belop2003, poppRequests.getInntekt(tomAar)!!.belop)
        assertEquals(belop2002, poppRequests.getInntekt(2002)!!.belop)
        assertEquals(belop2001, poppRequests.getInntekt(2001)!!.belop)
        assertEquals(belop2000, poppRequests.getInntekt(fomAar)!!.belop)
    }

    @Test
    fun `Given an unauthorized audience when calling post inntekt then return 401`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest())
                .header(HttpHeaders.AUTHORIZATION, createToken(audience = "unauthorizedAudience"))
                .header(ENVIRONMENT_HEADER, Q1)
                .header(NAV_CALL_ID, "test")
                .header(NAV_CONSUMER_ID, "test")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `Given an tomAr before fomAr when calling post inntekt then return 400 Bad Request`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(fomAar = 2000, tomAar = 1999))
                .header(HttpHeaders.AUTHORIZATION, createToken())
                .header(ENVIRONMENT_HEADER, Q1)
                .header(NAV_CALL_ID, "test")
                .header(NAV_CONSUMER_ID, "test")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Given empty environment header when calling post inntekt then return 400 Bad Request`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest())
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `when calling get environment then return environment`() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/environment"))
            .andExpect(status().isOk)
            .andReturn()

        val body = result.response.contentAsString
        Environment.values().forEach {
            assertTrue(body.contains(it.name), "Response did not contain environment ${it.name}")
        }
    }

    private fun inntektRequest(
        fnr: String? = "01234567890",
        fomAar: Int? = 2000,
        tomAar: Int? = 2001,
        belop: Long? = 100000L,
        redusertMedGrunnbelop: Boolean? = false
    ): String {
        return """
            {
                "fnr": "$fnr",
                "fomAar": $fomAar,
                "tomAar": $tomAar,
                "belop": $belop,
                "redusertMedGrunnbelop": $redusertMedGrunnbelop
            }
        """
    }

    private fun performPostInntekt(environment: String = Q1, request: String = inntektRequest()) {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(request)
                .header(HttpHeaders.AUTHORIZATION, createToken())
                .header(ENVIRONMENT_HEADER, environment)
                .header(NAV_CALL_ID, "test")
                .header(NAV_CONSUMER_ID, "test")
        )
            .andExpect(status().isOk)
    }

    private fun createToken(audience: String = ACCEPTED_AUDIENCE): String {
        return "Bearer ${
            server.issueToken(
                issuerId = "aad",
                clientId = "client",
                tokenCallback = DefaultOAuth2TokenCallback(issuerId = "aad", audience = listOf(audience))
            ).serialize()
        }"
    }

    private fun List<LagreInntektPoppRequest>.getInntekt(ar: Int) =
        this.map { it.inntekt }.firstOrNull { it.inntektAr == ar }

    companion object {
        private const val ACCEPTED_AUDIENCE = "testaud"

        private val urlEqualToPoppQ1 = urlEqualTo("/q1/inntekt")
        private val urlEqualToPoppQ2 = urlEqualTo("/q2/inntekt")

        private val Q1 = Environment.Q1.name
        private val Q2 = Environment.Q2.name


        private val wiremock = WireMockServer(WireMockSpring.options().port(9991)).also { it.start() }

        @JvmStatic
        @AfterAll
        fun clean() {
            wiremock.shutdown()
        }
    }
}
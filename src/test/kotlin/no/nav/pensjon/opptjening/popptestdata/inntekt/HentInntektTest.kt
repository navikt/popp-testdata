package no.nav.pensjon.opptjening.popptestdata.inntekt

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import no.nav.pensjon.opptjening.popptestdata.PoppTestdataApp
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor
import no.nav.pensjon.opptjening.popptestdata.config.MockPoppTokenProviderConfig
import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.Inntekt
import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [PoppTestdataApp::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
class HentInntektTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var server: MockOAuth2Server

    @BeforeEach
    fun resetWiremock() {
        wiremock.resetAll()
    }

    @Test
    fun `Given sumPi returns 200 with no body when calling get inntekt then return 200 ok with empty list`() {
        wiremock.stubFor(WireMock.post(urlSumPiQ1).willReturn(WireMock.aResponse().withStatus(200)))

        val inntektList = performGetInntekt().andExpect(MockMvcResultMatchers.status().isOk).hentInntektResponse()
        assertEquals(0, inntektList.size)
    }

    @Test
    fun `Given sumPi returns 201 with no body when calling get inntekt then return 200 ok with empty list`() {
        wiremock.stubFor(WireMock.post(urlSumPiQ1).willReturn(WireMock.aResponse().withStatus(201)))

        val inntektList = performGetInntekt().andExpect(MockMvcResultMatchers.status().isOk).hentInntektResponse()
        assertEquals(0, inntektList.size)
    }

    @Test
    fun `Given sumPi returns 200 with two inntekts when calling get inntekt then return 200 ok with two inntekts`() {
        wiremock.stubFor(
            WireMock.post(urlSumPiQ1).willReturn(
                WireMock.aResponse().withStatus(200).withBody(
                    """
                {
                  "inntekter": [
                    {
                      "inntektAr": 2000,
                      "belop": 100000,
                      "inntektType": "SUM_PI"
                    },
                    {
                      "inntektAr": 2001,
                      "belop": 200000,
                      "inntektType": "SUM_PI"
                    }
                  ]
                }
            """.trimIndent()
                ).withHeader("content-type", "application/json")
            )
        )

        val inntektList = performGetInntekt().andExpect(MockMvcResultMatchers.status().isOk).hentInntektResponse()
        assertEquals(2000, inntektList[0].inntektAar)
        assertEquals(100000, inntektList[0].belop)
        assertEquals(2001, inntektList[1].inntektAar)
        assertEquals(200000, inntektList[1].belop)
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1`() {
        wiremock.stubFor(WireMock.post(urlSumPiQ1).willReturn(WireMock.aResponse().withStatus(200)))

        val inntektList = performGetInntekt()
            .andExpect(MockMvcResultMatchers.status().isOk)
            .hentInntektResponse()

        assertEquals(0, inntektList.size)
        wiremock.verify(
            WireMock.postRequestedFor(urlSumPiQ1)
                .withHeader("Authorization", WireMock.equalTo("Bearer ${MockPoppTokenProviderConfig.POPP_Q1_TOKEN}"))
        )
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        wiremock.stubFor(WireMock.post(urlSumPiQ2).willReturn(WireMock.aResponse().withStatus(200)))

        val inntektList = performGetInntekt(environment = Q2)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .hentInntektResponse()

        assertEquals(0, inntektList.size)
        wiremock.verify(
            WireMock.postRequestedFor(urlSumPiQ2)
                .withHeader("Authorization", WireMock.equalTo("Bearer ${MockPoppTokenProviderConfig.POPP_Q2_TOKEN}"))
        )
    }

    fun ResultActions.hentInntektResponse() = jacksonObjectMapper().readValue(
        this.andReturn().response.contentAsString,
        object : TypeReference<List<Inntekt>>() {}
    )

    private fun performGetInntekt(
        fnr: String? = "12345678901",
        token: String = createToken(),
        environment: String? = Q1,
        navCallId: String? = "test",
        navConsumerId: String? = "test"
    ) =
        mockMvc.perform(
            MockMvcRequestBuilders.get("/inntekt")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, token)
                .apply {
                    environment?.let { header(TokenInterceptor.ENVIRONMENT_HEADER, environment) }
                    navCallId?.let { header(HeaderInterceptor.NAV_CALL_ID, navCallId) }
                    navConsumerId?.let { header(HeaderInterceptor.NAV_CONSUMER_ID, navConsumerId) }
                    fnr?.let { header("fnr", fnr) }
                }
        )

    private fun createToken(audience: String = ACCEPTED_AUDIENCE): String {
        return "Bearer ${
            server.issueToken(
                issuerId = "aad",
                clientId = "client",
                tokenCallback = DefaultOAuth2TokenCallback(issuerId = "aad", audience = listOf(audience))
            ).serialize()
        }"
    }

    companion object {
        private const val ACCEPTED_AUDIENCE = "testaud"

        private val urlSumPiQ1 = WireMock.urlEqualTo("/q1/inntekt/sumPi")
        private val urlSumPiQ2 = WireMock.urlEqualTo("/q2/inntekt/sumPi")

        private val Q1 = Environment.Q1.name
        private val Q2 = Environment.Q2.name

        private val wiremock = WireMockServer(WireMockSpring.options().port(9991)).also { it.start() }

        @JvmStatic
        @AfterAll
        fun clean() {
            wiremock.stop()
            wiremock.shutdown()
        }
    }

}
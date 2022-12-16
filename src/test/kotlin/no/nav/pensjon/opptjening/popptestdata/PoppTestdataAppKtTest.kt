package no.nav.pensjon.opptjening.popptestdata

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.pensjon.opptjening.popptestdata.MockAzureTokenConfig.Companion.POPP_Q1_TOKEN
import no.nav.pensjon.opptjening.popptestdata.MockAzureTokenConfig.Companion.POPP_Q2_TOKEN
import no.nav.pensjon.opptjening.popptestdata.common.environment.Environment
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
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
        wiremock.stubFor(post(urlEqualTo(POPP_INNTEKT_Q1_URL)).willReturn(aResponse().withStatus(200)))

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q1))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1`() {
        wiremock.stubFor(post(urlEqualTo(POPP_INNTEKT_Q1_URL)).willReturn(aResponse().withStatus(200)))

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q1))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isOk)

        wiremock.verify(postRequestedFor(urlEqualTo(POPP_INNTEKT_Q1_URL)))
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        wiremock.stubFor(post(urlEqualTo(POPP_INNTEKT_Q2_URL)).willReturn(aResponse().withStatus(200)))

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q2))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isOk)

        wiremock.verify(postRequestedFor(urlEqualTo(POPP_INNTEKT_Q2_URL)))
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1 with Q1 token`() {
        wiremock.stubFor(post(urlEqualTo(POPP_INNTEKT_Q1_URL)).willReturn(aResponse().withStatus(200)))

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q1))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isOk)

        wiremock.verify(
            postRequestedFor(urlEqualTo(POPP_INNTEKT_Q1_URL))
                .withHeader("Authorization", equalTo("Bearer $POPP_Q1_TOKEN"))
        )
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2 with Q2 token`() {
        wiremock.stubFor(post(urlEqualTo(POPP_INNTEKT_Q2_URL)).willReturn(aResponse().withStatus(200)))

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q2))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isOk)

        wiremock.verify(
            postRequestedFor(urlEqualTo(POPP_INNTEKT_Q2_URL))
                .withHeader("Authorization", equalTo("Bearer $POPP_Q2_TOKEN"))
        )
    }

    @Test
    fun `Given an unauthorized audience when calling post inntekt then return 401`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q1))
                .header(HttpHeaders.AUTHORIZATION, createToken(audience = "unauthorizedAudience"))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `Given an tomAr before fomAr when calling post inntekt then return 400 Bad Request`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(inntektRequest(environment = Q1, fomAar = 2000, tomAar = 1999))
                .header(HttpHeaders.AUTHORIZATION, createToken())
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `when calling get environment then return environment`(){
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/environment"))
            .andExpect(status().isOk)
            .andReturn()


        val body = result.response.contentAsString
        Environment.values().forEach {
            assertTrue(body.contains(it.name), "Response did not contain environment ${it.name}")
        }

    }

    private fun inntektRequest(
        environment: String? = Q1,
        fnr: String? = "01234567890",
        fomAar: Int? = 2000,
        tomAar: Int? = 2001,
        belop: Long? = 100000L,
        redusertMedGrunnbelop: Boolean? = false
    ): String {
        return """
            {
                "environment": "$environment",
                "fnr": "$fnr",
                "fomAar": $fomAar,
                "tomAar": $tomAar,
                "belop": $belop,
                "redusertMedGrunnbelop": $redusertMedGrunnbelop
            }
        """
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

    companion object {
        private const val ACCEPTED_AUDIENCE = "testaud"
        private const val POPP_INNTEKT_Q1_URL = "/q1/inntekt"
        private const val POPP_INNTEKT_Q2_URL = "/q2/inntekt"
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
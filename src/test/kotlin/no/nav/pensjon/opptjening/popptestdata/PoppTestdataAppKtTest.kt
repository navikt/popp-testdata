package no.nav.pensjon.opptjening.popptestdata

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.nimbusds.jose.JOSEObjectType
import no.nav.pensjon.opptjening.popptestdata.api.Environment
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.WireMockSpring
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest(classes = [PoppTestdataApp::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@EnableMockOAuth2Server
@ActiveProfiles(profiles = ["local"])
internal class PoppTestdataAppKtTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var server: MockOAuth2Server

    @BeforeEach
    fun resetAll() {
        wiremock.resetAll()
    }


    @Test
    fun `Given an unauthrorized audience when calling post inntekt then return 401`() {
        stubWiremock200(poppInntektQ1Url)

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest())
                .header(HttpHeaders.AUTHORIZATION, token("banan"))
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `Given a valid lagreInntekt request when calling post inntekt then return 200 ok`() {
        stubWiremock200(poppInntektQ1Url)

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest())
                .header(HttpHeaders.AUTHORIZATION, token(acceptedAudience))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1`() {
        stubWiremock200(poppInntektQ1Url)

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest(environment = "Q1"))
                .header(HttpHeaders.AUTHORIZATION, token(acceptedAudience))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        wiremock.verify(1, postRequestedFor(urlEqualTo(poppInntektQ1Url)))
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        stubWiremock200(poppInntektQ2Url)

        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest(environment = "Q2"))
                .header(HttpHeaders.AUTHORIZATION, token(acceptedAudience))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

        wiremock.verify(1, postRequestedFor(urlEqualTo(poppInntektQ2Url)))
    }

    private fun stubWiremock200(url: String){
        wiremock.stubFor(
            WireMock.post(urlEqualTo(url))
                .willReturn(aResponse().withStatus(200))
        )
    }


    private fun createLagreInntektRequest(
        environment: String? = Environment.Q1.name,
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

    private fun token(audience: String): String {
        return "Bearer ${
            server.issueToken(
                issuerId = "aad",
                clientId = "theclientid",
                tokenCallback = DefaultOAuth2TokenCallback(
                    "aad",
                    "random",
                    JOSEObjectType.JWT.type,
                    listOf(audience),
                    emptyMap(),
                    3600
                )
            ).serialize()
        }"
    }

    companion object {
        private const val acceptedAudience = "testaud"
        private const val poppInntektQ1Url = "/q1/inntekt"
        private const val poppInntektQ2Url = "/q2/inntekt"

        private val wiremock = WireMockServer(WireMockSpring.options().port(9991)).also { it.start() }

        @JvmStatic
        @AfterAll
        fun clean() {
            wiremock.shutdown()
        }
    }
}
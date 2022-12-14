package no.nav.pensjon.opptjening.popptestdata

//import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.nimbusds.jose.JOSEObjectType
import no.nav.pensjon.opptjening.popptestdata.api.Environment
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [PoppTestdataApp::class])
@AutoConfigureMockMvc
@DirtiesContext
@EnableMockOAuth2Server
//@WireMockTest(httpPort = 4567)
@ActiveProfiles(profiles = ["local"])
internal class PoppTestdataAppKtTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var server: MockOAuth2Server


    @BeforeEach
    fun resetMock(){
        //poppMock.reset()
    }

    @Test
    fun `Given an unauthrorized audience when calling post inntekt then return 401`() {
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
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest())
                .header(HttpHeaders.AUTHORIZATION, token(acceptedAudience))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        mockMvc.perform(
            post("/inntekt")
                .contentType(APPLICATION_JSON)
                .content(createLagreInntektRequest())
                .header(HttpHeaders.AUTHORIZATION, token(acceptedAudience))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
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
    }
}
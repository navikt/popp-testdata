package no.nav.pensjon.opptjening.popptestdata.inntekt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.nav.pensjon.opptjening.popptestdata.PoppTestdataApp
import no.nav.pensjon.opptjening.popptestdata.common.DEFAULT_CHANGED_BY
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CALL_ID
import no.nav.pensjon.opptjening.popptestdata.common.HeaderInterceptor.Companion.NAV_CONSUMER_ID
import no.nav.pensjon.opptjening.popptestdata.config.MockPoppTokenProviderConfig.Companion.POPP_Q1_TOKEN
import no.nav.pensjon.opptjening.popptestdata.config.MockPoppTokenProviderConfig.Companion.POPP_Q2_TOKEN
import no.nav.pensjon.opptjening.popptestdata.environment.Environment
import no.nav.pensjon.opptjening.popptestdata.inntekt.InntektController.Companion.INNTEKT_PATH
import no.nav.pensjon.opptjening.popptestdata.inntekt.model.*
import no.nav.pensjon.opptjening.popptestdata.token.TokenInterceptor.Companion.MILJO
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest(classes = [PoppTestdataApp::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
internal class LagreInntektTest {

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

        performPostInntekt().andExpect(status().isOk)
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1`() {
        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q1).andExpect(status().isOk)

        wiremock.verify(postRequestedFor(urlEqualToPoppQ1))
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2`() {
        wiremock.stubFor(post(urlEqualToPoppQ2).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q2).andExpect(status().isOk)

        wiremock.verify(postRequestedFor(urlEqualToPoppQ2))
    }

    @Test
    fun `Given env Q1 when calling post inntekt then call lagre inntekt in popp Q1 with Q1 token`() {
        wiremock.stubFor(post(urlEqualToPoppQ1).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q1).andExpect(status().isOk)

        wiremock.verify(
            postRequestedFor(urlEqualToPoppQ1)
                .withHeader("Authorization", equalTo("Bearer $POPP_Q1_TOKEN"))
        )
    }

    @Test
    fun `Given env Q2 when calling post inntekt then call lagre inntekt in popp Q2 with Q2 token`() {
        wiremock.stubFor(post(urlEqualToPoppQ2).willReturn(aResponse().withStatus(200)))

        performPostInntekt(environment = Q2).andExpect(status().isOk)

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

        performPostInntekt(request = inntektRequest(fomAar = fomAar, tomAar = tomAar)).andExpect(status().isOk)

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

        performPostInntekt(request = jacksonObjectMapper().writeValueAsString(request)).andExpect(status().isOk)

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
            request = inntektRequest(fomAar = fomAar, tomAar = tomAar, belop = belop2003, redusertMedGrunnbelop = true)
        ).andExpect(status().isOk)

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
        performPostInntekt(token = createToken(audience = "unauthorizedAudience")).andExpect(status().isUnauthorized)
    }

    @Test
    fun `Given an tomAr before fomAr when calling post inntekt then return 400 Bad Request`() {
        performPostInntekt(request = inntektRequest(fomAar = 2000, tomAar = 1999)).andExpect(status().isBadRequest)
    }

    @Test
    fun `Given empty environment header when calling post inntekt then return 400 Bad Request`() {
        performPostInntekt(environment = null).andExpect(status().isBadRequest)
    }

    @Test
    fun `Given empty navCallId header when calling post inntekt then return 400 Bad Request`() {
        performPostInntekt(navCallId = null).andExpect(status().isBadRequest)
    }

    @Test
    fun `Given empty navConsumerId header when calling post inntekt then return 400 Bad Request`() {
        performPostInntekt(navConsumerId = null).andExpect(status().isBadRequest)
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

    private fun performPostInntekt(
        request: String = inntektRequest(),
        token: String = createToken(),
        environment: String? = Q1,
        navCallId: String? = "test",
        navConsumerId : String? = "test"
    ) =
        mockMvc.perform(
            post(INNTEKT_PATH)
                .contentType(APPLICATION_JSON)
                .content(request)
                .header(HttpHeaders.AUTHORIZATION, token)
                .apply {
                    environment?.let { header(MILJO, environment) }
                    navCallId?.let { header(NAV_CALL_ID, navCallId) }
                    navConsumerId?.let { header(NAV_CONSUMER_ID, navConsumerId) }
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

    private fun List<LagreInntektPoppRequest>.getInntekt(ar: Int) =
        this.map { it.inntekt }.firstOrNull { it.inntektAr == ar }

    companion object {
        private const val ACCEPTED_AUDIENCE = "testaud"

        private val urlEqualToPoppQ1 = urlEqualTo("/q1/inntekt")
        private val urlEqualToPoppQ2 = urlEqualTo("/q2/inntekt")

        private val Q1 = Environment.q1.name
        private val Q2 = Environment.q2.name

        private val wiremock = WireMockServer(WireMockSpring.options().port(9991)).also { it.start() }

        @JvmStatic
        @AfterAll
        fun clean() {
            wiremock.stop()
            wiremock.shutdown()
        }
    }
}
package no.nav.pensjon.opptjening.popptestdata.miljo

import no.nav.pensjon.opptjening.popptestdata.PoppTestdataApp
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(classes = [PoppTestdataApp::class])
@AutoConfigureMockMvc
@EnableMockOAuth2Server
internal class MiljoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `when calling get miljo endpoint then return miljo`() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/miljo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val body = result.response.contentAsString

        Miljo.values().forEach {
            assertTrue(body.contains(it.name), "Response did not contain environment ${it.name}")
        }
    }
}


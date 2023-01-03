package no.nav.pensjon.opptjening.popptestdata.health

import no.nav.pensjon.opptjening.popptestdata.PoppTestdataApp
import no.nav.pensjon.opptjening.popptestdata.environment.Miljo
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
internal class HealthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `when calling get environment then return environment`() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/miljo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val body = result.response.contentAsString
        Miljo.values().forEach {
            assertTrue(body.contains(it.name), "Response did not contain environment ${it.name}")
        }
    }

    @Test
    fun `when calling ping endpoint then return 200`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/ping"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
    }

    @Test
    fun `when isAlive ping endpoint then return 200`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/internal/isalive"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
    }

    @Test
    fun `when calling isReady endpoint then return 200`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/internal/isready"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
    }
}
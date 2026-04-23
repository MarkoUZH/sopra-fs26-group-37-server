package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.TranslationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranslationController.class)
public class TranslationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TranslationService translationService;

    @Test
    public void getTranslation_validInput_success() throws Exception {
        // given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", "Hello");
        requestBody.put("language", "de");
        requestBody.put("sourceLanguage", "en");

        String translatedText = "Hallo";

        given(translationService.translate("Hello", "en", "de")).willReturn(translatedText);

        // when
        MockHttpServletRequestBuilder postRequest = post("/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody))
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(content().string(translatedText));
    }

    @Test
    public void getTranslation_defaultsUsed_success() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", "Hello");

        String translatedText = "Hello";

        given(translationService.translate("Hello", "en", "en")).willReturn(translatedText);

        // when
        MockHttpServletRequestBuilder postRequest = post("/translate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestBody))
                .header("Authorization", "Bearer test-token");

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(content().string(translatedText));
    }

    /**
     * Helper Method to convert objects into a JSON string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
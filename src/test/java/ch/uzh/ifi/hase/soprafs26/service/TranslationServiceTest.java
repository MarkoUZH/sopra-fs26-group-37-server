package ch.uzh.ifi.hase.soprafs26.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @InjectMocks
    private TranslationService translationService;

    @Mock
    private RestTemplate restTemplate;

    private static final String API_URL = "https://router.huggingface.co/v1/chat/completions";

    @BeforeEach
    void setUp() {
        // Inject the fake API token and the mock RestTemplate
        ReflectionTestUtils.setField(translationService, "hfToken", "test-token-123");
        ReflectionTestUtils.setField(translationService, "restTemplate", restTemplate);
    }

    /** Builds a minimal but valid OpenAI-style chat-completion response body. */
    private Map<String, Object> buildSuccessResponse(String translatedText) {
        return Map.of(
                "choices", List.of(
                        Map.of("message", Map.of("content", translatedText))
                )
        );
    }

    private ResponseEntity<Map> okResponse(String translatedText) {
        return new ResponseEntity<>(buildSuccessResponse(translatedText), HttpStatus.OK);
    }

    @Test
    void translate_returnsTranslatedText() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(okResponse("Bonjour le monde"));

        String result = translationService.translate("Hello world", "English", "French");

        assertEquals("Bonjour le monde", result);
    }

    @Test
    void translate_callsApiExactlyOnce() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(okResponse("Hola"));

        translationService.translate("Hello", "English", "Spanish");

        verify(restTemplate, times(1))
                .postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void translate_requestContainsAuthorizationHeader() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> entity = invocation.getArgument(1);
                    assertTrue(entity.getHeaders().containsHeader("Authorization"),
                            "Authorization header must be present");
                    String authValue = entity.getHeaders().getFirst("Authorization");
                    assertTrue(authValue != null && authValue.startsWith("Bearer "),
                            "Authorization header must start with 'Bearer '");
                    return okResponse("Ciao");
                });

        translationService.translate("Hello", "English", "Italian");
    }

    @Test
    void translate_requestContainsBillToHeader() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<?> entity = invocation.getArgument(1);
                    assertTrue(entity.getHeaders().containsHeader("X-HF-Bill-To"),
                            "X-HF-Bill-To header must be present");
                    return okResponse("Ciao");
                });

        translationService.translate("Hello", "English", "Italian");
    }

    @Test
    void translate_requestBodyContainsSourceAndTargetLanguage() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<Map<String, Object>> entity = invocation.getArgument(1);
                    Map<String, Object> body = entity.getBody();
                    assertNotNull(body);

                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> messages =
                            (List<Map<String, String>>) body.get("messages");
                    assertNotNull(messages);
                    assertFalse(messages.isEmpty());

                    // System prompt should mention both languages
                    String systemContent = messages.get(0).get("content");
                    assertTrue(systemContent.contains("English"),
                            "System prompt must reference source language");
                    assertTrue(systemContent.contains("German"),
                            "System prompt must reference target language");

                    // User message should carry the original text
                    String userContent = messages.get(1).get("content");
                    assertEquals("Hello", userContent);

                    return okResponse("Hallo");
                });

        translationService.translate("Hello", "English", "German");
    }

    @Test
    void translate_differentLanguagePairsProduceDistinctSystemPrompts() {
        // Capture the two system prompts to confirm they differ
        final String[] capturedPrompts = new String[2];

        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenAnswer(invocation -> {
                    HttpEntity<Map<String, Object>> entity = invocation.getArgument(1);
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> messages =
                            (List<Map<String, String>>) entity.getBody().get("messages");
                    return okResponse(messages.get(0).get("content")); // return prompt as "translation" for assertion
                });

        String prompt1 = translationService.translate("Hi", "English", "French");
        String prompt2 = translationService.translate("Hi", "English", "German");

        assertNotEquals(prompt1, prompt2,
                "System prompts for different language pairs must differ");
    }

    @Test
    void translate_emptyChoicesList_returnsErrorString() {
        Map<String, Object> emptyChoices = Map.of("choices", List.of());
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(emptyChoices, HttpStatus.OK));

        String result = translationService.translate("Hello", "English", "French");

        assertTrue(result.startsWith("Error:"),
                "Empty choices list should yield an error string, got: " + result);
    }

    @Test
    void translate_http401_throwsResponseStatusException() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> translationService.translate("Hello", "English", "French"));

        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
    }

    @Test
    void translate_http400_throwsResponseStatusException() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> translationService.translate("Hello", "English", "French"));

        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
    }

    @Test
    void translate_http404_throwsResponseStatusException() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> translationService.translate("Hello", "English", "French"));

        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
    }

    @Test
    void translate_networkError_throwsResponseStatusException() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> translationService.translate("Hello", "English", "French"));

        assertEquals(HttpStatus.BAD_GATEWAY, ex.getStatusCode());
        assertTrue(ex.getReason() != null && ex.getReason().contains("LLM Translation Error"),
                "Reason should describe the LLM error");
    }

    @Test
    void translate_emptyInputText_stillCallsApi() {
        when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(okResponse(""));

        String result = translationService.translate("", "English", "French");

        // API was reached; result is whatever the model returns (empty string here)
        assertNotNull(result);
        verify(restTemplate, times(1))
                .postForEntity(eq(API_URL), any(HttpEntity.class), eq(Map.class));
    }
}
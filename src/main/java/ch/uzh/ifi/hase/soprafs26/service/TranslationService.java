package ch.uzh.ifi.hase.soprafs26.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TranslationService {

    @Value("${HUGGINGFACE_API_TOKEN}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL =
        "https://router.huggingface.co/hf-inference/models/google-t5/t5-base";

    public String translate(String text, String languageIso2) {
        // T5 uses natural-language prefixes: "translate English to German: [text]"
        String languageName = Locale.forLanguageTag(languageIso2).getDisplayLanguage(Locale.ENGLISH);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + hfToken);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", "translate English to " + languageName + ": " + text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Receive as String to avoid Spring failing on HF's "application/json, application/+json" Content-Type header
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            List<Map<String, String>> result = new ObjectMapper()
                .readValue(response.getBody(), new TypeReference<>() {});

            if (result != null && !result.isEmpty()) {
                return result.get(0).get("translation_text");
            }
            return "Translation result was empty";

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI Model Error: " + e.getMessage());
        }
    }
}
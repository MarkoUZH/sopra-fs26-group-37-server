package ch.uzh.ifi.hase.soprafs26.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class TranslationService {

    @Value("${HUGGINGFACE_API_TOKEN}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://api-inference.huggingface.co/models/facebook/nllb-200-distilled-1.3B";

    public String translate(String text, String language) {
        // Prepare Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + hfToken);

        // Prepare Request Body (NLLB format)
        Map<String, Object> body = new HashMap<>();
        body.put("inputs", text);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("language", language); // e.g., "fra_Latn"
        body.put("parameters", parameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // The model returns a List of Maps
            ResponseEntity<List> response = restTemplate.postForEntity(API_URL, entity, List.class);
            List<Map<String, String>> result = response.getBody();
            
            return result != null ? result.get(0).get("translation_text") : "Translation failed";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI Model Error: " + e.getMessage());
        }
    }
}
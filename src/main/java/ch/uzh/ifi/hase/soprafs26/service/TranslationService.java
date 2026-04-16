package ch.uzh.ifi.hase.soprafs26.service;

import ch.uzh.ifi.hase.soprafs26.utils.NllbLanguageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslationService {

    @Value("${HUGGINGFACE_API_TOKEN}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Ensure this URL has NO trailing spaces
    private final String API_URL = "https://api-inference.huggingface.co/models/facebook/nllb-200-distilled-1.3B";

    public String translate(String text, String languageIso2) {
        // 1. Transform ISO-2 (e.g. "de") into NLLB code (e.g. "deu_Latn")
        String nllbCode = NllbLanguageMapper.getCode(languageIso2);

        // 2. Prepare Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + hfToken);

        // 3. Prepare Request Body
        Map<String, Object> body = new HashMap<>();
        body.put("inputs", text);
        
        Map<String, String> parameters = new HashMap<>();
        // NLLB-200 inference specifically looks for 'tgt_lang'
        parameters.put("tgt_lang", nllbCode); 
        body.put("parameters", parameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // NLLB returns a List of Maps: [{"translation_text": "..."}]
            // Note: If the model is loading, this will throw a 503 error
            ResponseEntity<List> response = restTemplate.postForEntity(API_URL, entity, List.class);
            
            @SuppressWarnings("unchecked")
            List<Map<String, String>> result = response.getBody();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0).get("translation_text");
            }
            return "Translation result was empty";
            
        } catch (Exception e) {
            // If you get a 503, the model is just "warming up"
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI Model Error: " + e.getMessage());
        }
    }
}
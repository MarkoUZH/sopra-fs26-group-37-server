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
import java.util.Map;
@Service
public class TranslationService {

    @Value("${huggingface.api.token}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // Use the standard OpenAI-compatible router URL
private static final String API_URL = "https://router.huggingface.co/v1/chat/completions";
    public String translate(String text, String sourceLang, String targetLang) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + hfToken);
        
        // CRITICAL: Tell HF to bill the University Organization
        // Replace "uzh-org-name" with your actual Org slug on Hugging Face
        headers.set("X-HF-Bill-To", "UZHedu"); 

        Map<String, Object> body = new HashMap<>(); 
        // Use :fastest to let the router pick the best provider (Groq, Together, etc.)
        body.put("model", "openai/gpt-oss-120b");
        
        body.put("messages", List.of(
            Map.of("role", "system", "content", 
                String.format("You are a professional translator. Translate from %s to %s. Output ONLY the translated text. If the text is already in %s, just output it as is.", sourceLang, targetLang, targetLang)),
            Map.of("role", "user", "content", text)
        ));
        
        body.put("temperature", 0.1); // Low temperature = more accurate translation
        body.put("max_tokens", 1024);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Using Map.class is fine, but we need to navigate the tree
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
            
            if (response.getBody() == null) return "Error: Null response";

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices == null || choices.isEmpty()) return "Error: No choices in response";

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            // This will catch 401 (Token), 400 (Org Billing), or 404 (Model)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "LLM Translation Error: " + e.getMessage());
        }
    }
}
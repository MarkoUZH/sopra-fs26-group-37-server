package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.TranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    /**
     * Universal Translation Endpoint
     * Use this for Task Titles, Descriptions, or UI elements.
     * Request body: { "text": "Something to translate", "targetLang": "de" }
     */
    @PostMapping("/translate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getTranslation(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String targetLang = request.get("targetLang");
        
        // If the frontend didn't send a language, we can default to English
        if (targetLang == null || targetLang.isEmpty()) {
            targetLang = "en";
        }
        
        return translationService.translate(text, targetLang);
    }
}
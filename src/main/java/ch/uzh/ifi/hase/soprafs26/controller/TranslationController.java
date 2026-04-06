package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.service.TranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class TranslationController {

    private final TranslationService translationService;

    // Constructor injection is the standard for SoPra
    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/translate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getTranslation(@RequestBody Map<String, String> request) {
        // The frontend will send { "text": "...", "targetLang": "..." }
        String text = request.get("text");
        String targetLang = request.get("targetLang");
        
        return translationService.translate(text, targetLang);
    }
}
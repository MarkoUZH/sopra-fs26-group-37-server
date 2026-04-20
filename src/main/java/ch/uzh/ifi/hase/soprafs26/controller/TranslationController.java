package ch.uzh.ifi.hase.soprafs26.controller;
import ch.uzh.ifi.hase.soprafs26.service.UserService;
import ch.uzh.ifi.hase.soprafs26.service.TranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class TranslationController {

    private final TranslationService translationService;
    private final UserService userService;

    public TranslationController(TranslationService translationService, UserService userService) {
        this.translationService = translationService;
        this.userService= userService;
    }

    /**
     * Universal Translation Endpoint
     * Use this for Task Titles, Descriptions, or UI elements.
     * Request body: { "text": "Something to translate", "language": "de", "sourceLanguage": "en" }
     */
    @PostMapping("/translate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getTranslation(
    @RequestBody Map<String, String> request,
    @RequestHeader(value = "Authorization", required = false) String token 
    ) {
        //userService.verifyToken(token);

        String text = request.get("text");
        String targetLang = request.get("language"); // Your frontend currently sends 'language'
        String sourceLang = request.get("sourceLanguage"); // Add this!
        
        // Default target language
        if (targetLang == null || targetLang.isEmpty()) {
            targetLang = "en";
        }
        
        // Default source language
        if (sourceLang == null || sourceLang.isEmpty()) {
            sourceLang = "en";
        }
        
        // Call the overloaded method with 3 parameters
        return translationService.translate(text, sourceLang, targetLang);
    }
}
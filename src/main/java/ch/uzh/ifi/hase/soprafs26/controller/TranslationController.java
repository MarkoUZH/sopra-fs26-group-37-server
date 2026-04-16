package ch.uzh.ifi.hase.soprafs26.controller;
import ch.uzh.ifi.hase.soprafs26.service.UserService;


import ch.uzh.ifi.hase.soprafs26.service.TranslationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class TranslationController {

    private final TranslationService translationService;
    private final UserService userService; // 1. Declare it

    public TranslationController(TranslationService translationService, UserService userService) {
        this.translationService = translationService;
        this.userService= userService;
    }

    /**
     * Universal Translation Endpoint
     * Use this for Task Titles, Descriptions, or UI elements.
     * Request body: { "text": "Something to translate", "targetLang": "de" }
     */
    @PostMapping("/translate")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getTranslation(
    @RequestBody Map<String, String> request,
    @RequestHeader(value = "X-Task-Token", required = true) String token // Get token from header
    ) {
        userService.verifyToken(token);

        String text = request.get("text");
        String language = request.get("language");
        
        // If the frontend didn't send a language, we can default to English
        if (language == null || language.isEmpty()) {
            language = "en";
        }
        
        return translationService.translate(text, language);
    }
}
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

    @Value("${HUGGINGFACE_API_TOKEN}")
    private String hfToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // Updated to use the new Inference Providers router instead of the deprecated api-inference subdomain
    private static final String API_URL =
        "https://router.huggingface.co/hf-inference/models/facebook/mbart-large-50-many-to-many-mmt";

    // Language mapper for mBART compatibility (All 52 Supported Languages)
    private String getMBartLanguageCode(String standardCode) {
        return switch (standardCode.toLowerCase()) {
            case "ar" -> "ar_AR"; // Arabic
            case "cs" -> "cs_CZ"; // Czech
            case "de" -> "de_DE"; // German
            case "en" -> "en_XX"; // English
            case "es" -> "es_XX"; // Spanish
            case "et" -> "et_EE"; // Estonian
            case "fi" -> "fi_FI"; // Finnish
            case "fr" -> "fr_XX"; // French
            case "gu" -> "gu_IN"; // Gujarati
            case "hi" -> "hi_IN"; // Hindi
            case "it" -> "it_IT"; // Italian
            case "ja" -> "ja_XX"; // Japanese
            case "kk" -> "kk_KZ"; // Kazakh
            case "ko" -> "ko_KR"; // Korean
            case "lt" -> "lt_LT"; // Lithuanian
            case "lv" -> "lv_LV"; // Latvian
            case "my" -> "my_MM"; // Burmese
            case "ne" -> "ne_NP"; // Nepali
            case "nl" -> "nl_XX"; // Dutch
            case "ro" -> "ro_RO"; // Romanian
            case "ru" -> "ru_RU"; // Russian
            case "si" -> "si_LK"; // Sinhala
            case "tr" -> "tr_TR"; // Turkish
            case "vi" -> "vi_VN"; // Vietnamese
            case "zh" -> "zh_CN"; // Chinese
            case "af" -> "af_ZA"; // Afrikaans
            case "az" -> "az_AZ"; // Azerbaijani
            case "bn" -> "bn_IN"; // Bengali
            case "fa" -> "fa_IR"; // Persian
            case "he" -> "he_IL"; // Hebrew
            case "hr" -> "hr_HR"; // Croatian
            case "id" -> "id_ID"; // Indonesian
            case "ka" -> "ka_GE"; // Georgian
            case "km" -> "km_KH"; // Khmer
            case "mk" -> "mk_MK"; // Macedonian
            case "ml" -> "ml_IN"; // Malayalam
            case "mn" -> "mn_MN"; // Mongolian
            case "mr" -> "mr_IN"; // Marathi
            case "pl" -> "pl_PL"; // Polish
            case "ps" -> "ps_AF"; // Pashto
            case "pt" -> "pt_XX"; // Portuguese
            case "sv" -> "sv_SE"; // Swedish
            case "sw" -> "sw_KE"; // Swahili
            case "ta" -> "ta_IN"; // Tamil
            case "te" -> "te_IN"; // Telugu
            case "th" -> "th_TH"; // Thai
            case "tl" -> "tl_XX"; // Tagalog
            case "uk" -> "uk_UA"; // Ukrainian
            case "ur" -> "ur_PK"; // Urdu
            case "xh" -> "xh_ZA"; // Xhosa
            case "gl" -> "gl_ES"; // Galician
            case "sl" -> "sl_SI"; // Slovene
            default -> throw new IllegalArgumentException("Unsupported language code: " + standardCode);
        };
    }

    // Maintained for backward compatibility: defaults source language to English
    public String translate(String text, String languageIso2) {
        return translate(text, "en", languageIso2);
    }

    // New overloaded method to support changing the source language
    public String translate(String text, String sourceLanguageIso2, String targetLanguageIso2) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + hfToken);
        headers.set("X-HF-Bill-To", "UZHedu");
        
        // CRITICAL FIX: Bypass the Hugging Face cache. 
        // If an earlier request without correct parameters just echoed the text, HF will cache that response.
        headers.set("X-Use-Cache", "false");

        Map<String, Object> body = new HashMap<>();
        
        // mBART takes pure text as input without natural language prefixes
        body.put("inputs", text);

        // Parameters map to explicitly set source and target languages
        Map<String, String> parameters = new HashMap<>();
        parameters.put("src_lang", getMBartLanguageCode(sourceLanguageIso2)); 
        parameters.put("tgt_lang", getMBartLanguageCode(targetLanguageIso2));
        
        body.put("parameters", parameters);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // Receive as String to avoid Spring failing on HF's "application/json, application/+json" Content-Type header
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            List<Map<String, String>> result = new ObjectMapper()
                .readValue(response.getBody(), new TypeReference<>() {});

            if (result != null && !result.isEmpty()) {
                Map<String, String> resultMap = result.get(0);
                
                // The translation pipeline outputs "translation_text"
                if (resultMap.containsKey("translation_text")) {
                    return resultMap.get("translation_text");
                } 
                // Fallback: If HF treats the endpoint dynamically as generic "text2text-generation", 
                // it outputs "generated_text" instead.
                else if (resultMap.containsKey("generated_text")) {
                    return resultMap.get("generated_text");
                }
            }
            return "Translation result was empty";

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI Model Error: " + e.getMessage());
        }
    }

}
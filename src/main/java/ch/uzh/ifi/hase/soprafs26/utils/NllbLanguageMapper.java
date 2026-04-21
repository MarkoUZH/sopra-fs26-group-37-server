package ch.uzh.ifi.hase.soprafs26.utils;
import com.neovisionaries.i18n.LanguageCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NllbLanguageMapper {

    public static String getCode(String iso2Code) {
        if (iso2Code == null || iso2Code.isEmpty()) {
            return "eng_Latn"; // Default to English
        }

        // 1. Convert 2-letter (e.g., "de") to 3-letter (e.g., "deu")
        LanguageCode lc = LanguageCode.getByCode(iso2Code.toLowerCase());
        
        // Handle cases where iso-639-1 might send something the library doesn't know
        if (lc == null || lc.getAlpha3() == null) {
            return "eng_Latn";
        }

        String iso3 = lc.getAlpha3().toString();

        // 2. Determine the Script suffix required by NLLB-200
        // NLLB needs the script (Latn, Cyrl, etc.) to function correctly
        String script = switch (iso2Code.toLowerCase()) {
            case "ar", "fa", "ur", "ps", "sd", "ug" -> "Arab";
            case "zh" -> "Hans"; // Simplified Chinese
            case "ja" -> "Jpan";
            case "ko" -> "Hang";
            case "hi", "mr", "ne", "sa", "ks" -> "Deva";
            case "ru", "bg", "uk", "be", "sr", "mk", "kk", "ky", "tg" -> "Cyrl";
            case "el" -> "Grek";
            case "th" -> "Thai";
            case "hy" -> "Armn";
            case "ka" -> "Kat";
            case "bn", "as" -> "Beng";
            case "gu" -> "Gujr";
            case "ta" -> "Taml";
            case "te" -> "Telu";
            case "kn" -> "Knda";
            case "ml" -> "Mlym";
            case "pa" -> "Guru";
            case "or" -> "Orya";
            case "si" -> "Sinh";
            case "km" -> "Khmr";
            case "lo" -> "Laoo";
            case "my" -> "Mymr";
            case "am" -> "Ethi";
            case "iu" -> "Cans";
            case "chr" -> "Cher";
            default -> "Latn"; // Default script for most languages
        };

        return iso3 + "_" + script;
    }
}
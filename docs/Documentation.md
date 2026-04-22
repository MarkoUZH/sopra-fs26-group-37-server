---

## Translation Feature

### Overview

The server exposes a translation endpoint powered by the [google-t5/t5-base](https://huggingface.co/google-t5/t5-base) model, hosted via the [HuggingFace Inference API](https://huggingface.co/docs/inference-providers). Requests are routed through `router.huggingface.co`.

### How it works

1. The client sends a `POST /translate` request with a text and a target language code.
2. `TranslationService` converts the ISO-639-1 language code (e.g. `"de"`) into a full language name (e.g. `"German"`) using Java's built-in `Locale` class.
3. It builds a T5-style prompt: `"translate English to German: Hello world"` and sends it to the HuggingFace API.
4. The translated text is extracted from the `translation_text` field of the response and returned to the client.

### Endpoint

```
POST /translate
```

**Headers**

| Header         | Required | Description                        |
|----------------|----------|------------------------------------|
| `X-Task-Token` | Yes      | A valid user session token         |
| `Content-Type` | Yes      | `application/json`                 |

**Request body**

```json
{
  "text": "Hello world",
  "language": "de"
}
```

**Response**

```
"Hallo Welt"
```

**Example curl**

```bash
curl -X POST http://localhost:8080/translate \
  -H "Content-Type: application/json" \
  -H "X-Task-Token: <your-token>" \
  -d '{"text": "Hello world", "language": "de"}'
```

### Setup

The endpoint requires a HuggingFace API token with inference permissions.

**Local development** — create a `local.properties` file in the project root (already gitignored):

```properties
huggingface.api.token=hf_your_token_here
```

Then run normally with `./gradlew bootRun`. The token is picked up automatically.

**Production (App Engine)** — set the real token in `app.yaml`:

```yaml
env_variables:
  HUGGINGFACE_API_TOKEN: "hf_your_token_here"
```

Do not commit real tokens to git. Use a secret manager or CI/CD environment secrets for production.

### Limitations

**Supported languages (4 total)**

T5-base was only fine-tuned for translation between the following languages. Requests for any other language will not error, but the output will be nonsensical.

| Language   | ISO code |
|------------|----------|
| English    | `en`     |
| German     | `de`     |
| French     | `fr`     |
| Romanian   | `ro`     |

**Other known limitations**
- Translation is always **from English**. Translating non-English source text is not supported.
- T5-base is a small general-purpose model, not a dedicated translation model. Output quality is lower than modern translation APIs.
- The HuggingFace free tier has rate limits. Under heavy load, requests may be throttled.
- The first request after a period of inactivity may be slower due to model cold-start on the inference server.

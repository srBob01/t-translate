package ru.arsentiev.translator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.arsentiev.translator.dto.ResponseList;
import ru.arsentiev.translator.dto.TranslateRequestDto;
import ru.arsentiev.translator.dto.TranslateResponseDto;
import ru.arsentiev.translator.handler.AccessDeniedException;
import ru.arsentiev.translator.handler.LanguageNotFoundException;
import ru.arsentiev.translator.handler.TranslationException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final RestTemplate restTemplate;
    private final TranslateEntityService service;
    private final ExecutorService executorService;

    @Value("${yandex.api.key}")
    private String iamToken;
    @Value("${yandex.api.folder-id}")
    private String folderId;
    @Value("${yandex.api.translate-url}")
    private String translateUrl;

    public TranslateResponseDto translate(TranslateRequestDto request, String clientIp) {
        log.info("Starting translation for input: {}", request.getInputText());

        List<String> words = Arrays.asList(request.getInputText().split("\\s+"));

        List<Future<String>> futures = words.stream()
                .map(word -> executorService.submit(() -> translateWord(word, request.getTranslatedLang(), request.getInputLang(), translateUrl)))
                .toList();

        List<String> translatedWords = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        String translatedText = String.join(" ", translatedWords);
        log.info("Translation completed. Translated text: {}", translatedText);

        return service.save(request, clientIp, translatedText);
    }

    private String translateWord(String word, String targetLang, String sourceLang, String translateUrl) {
        String requestBody = String.format(
                "{\"sourceLanguageCode\":\"%s\",\"targetLanguageCode\":\"%s\",\"texts\":[\"%s\"],\"folderId\":\"%s\"}",
                sourceLang, targetLang, word, folderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(iamToken);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseList response = restTemplate.postForObject(translateUrl, entity, ResponseList.class);
            if (response != null && response.getTranslations() != null && !response.getTranslations().isEmpty()) {
                return response.getTranslations().get(0).getText();
            } else {
                log.warn("No translation found for word: {}", word);
                return word;
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status.value() == HttpStatus.BAD_REQUEST.value()) {
                String responseBody = e.getResponseBodyAsString();
                log.error("Invalid language code: source: {}, target: {}", sourceLang, targetLang);

                if (responseBody.contains("sourceLanguageCode")) {
                    throw new LanguageNotFoundException(sourceLang, "source");
                } else {
                    throw new LanguageNotFoundException(targetLang, "target");
                }
            } else if (status.value() == HttpStatus.UNAUTHORIZED.value()) {
                log.error("Unauthorized access: check your IAM token");
                throw new AccessDeniedException();
            } else {
                log.error("HTTP error during translation request: {}", e.getMessage());
                throw new TranslationException("HTTP error during translation");
            }
        }
    }

}
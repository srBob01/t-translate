package ru.arsentiev.translator.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.arsentiev.translator.dto.TranslateRequestDto;
import ru.arsentiev.translator.dto.TranslateResponseDto;
import ru.arsentiev.translator.service.TranslationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public TranslateResponseDto translate(@RequestBody TranslateRequestDto request, HttpServletRequest httpRequest) {
        String clientIp = httpRequest.getRemoteAddr();
        return translationService.translate(request, clientIp);
    }
}

package ru.arsentiev.translator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.arsentiev.translator.dto.TranslateRequestDto;
import ru.arsentiev.translator.dto.TranslateResponseDto;
import ru.arsentiev.translator.entity.TranslateEntity;
import ru.arsentiev.translator.mapper.TranslateDtoMapper;
import ru.arsentiev.translator.repository.TranslateEntityRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslateEntityService {
    private final TranslateEntityRepository repository;
    private final TranslateDtoMapper mapper;

    @Transactional
    public TranslateResponseDto save(TranslateRequestDto request, String clientIp, String translatedText) {
        log.info("Saving translation for IP: {} with text: {}", clientIp, translatedText);
        TranslateEntity entity = mapper.requestDtoToEntity(request);
        entity.setIpAddress(clientIp);
        entity.setTranslatedText(translatedText);
        return Optional.of(entity)
                .map(repository::save)
                .map(mapper::entityToResponseDto)
                .orElseThrow(() -> {
                    log.error("Failed to save translation entity");
                    return new RuntimeException("Failed to save translation entity");
                });
    }
}

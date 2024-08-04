package ru.arsentiev.translator.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.arsentiev.translator.dto.TranslateRequestDto;
import ru.arsentiev.translator.dto.TranslateResponseDto;
import ru.arsentiev.translator.entity.TranslateEntity;

@Mapper(componentModel = "spring")
public interface TranslateDtoMapper {
    TranslateResponseDto entityToResponseDto(TranslateEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "translatedText", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    TranslateEntity requestDtoToEntity(TranslateRequestDto request);
}

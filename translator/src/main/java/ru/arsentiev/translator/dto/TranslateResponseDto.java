package ru.arsentiev.translator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslateResponseDto {
    private Long id;
    private String ipAddress;
    private String inputText;
    private String translatedText;
    private String inputLang;
    private String translatedLang;
    private LocalDateTime timestamp;
}

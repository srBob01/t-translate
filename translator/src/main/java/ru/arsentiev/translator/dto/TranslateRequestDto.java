package ru.arsentiev.translator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslateRequestDto {
    private String inputText;
    private String inputLang;
    private String translatedLang;
}

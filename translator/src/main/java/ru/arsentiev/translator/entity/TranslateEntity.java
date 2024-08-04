package ru.arsentiev.translator.entity;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TranslateEntity {
    @EqualsAndHashCode.Include
    private Long id;
    private String ipAddress;
    private String inputText;
    private String translatedText;
    private String inputLang;
    private String translatedLang;
    private LocalDateTime timestamp;
}

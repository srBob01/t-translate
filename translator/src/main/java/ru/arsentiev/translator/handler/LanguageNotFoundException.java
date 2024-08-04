package ru.arsentiev.translator.handler;

import lombok.Getter;

@Getter
public class LanguageNotFoundException extends RuntimeException {
    private final String language;
    private final String type;

    public LanguageNotFoundException(String language, String type) {
        super(String.format("Language not found: %s (%s)", language, type));
        this.language = language;
        this.type = type;
    }

}
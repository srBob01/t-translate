package ru.arsentiev.translator.handler;

public class TranslationException extends RuntimeException {
    public TranslationException(String message) {
        super(message);
    }
}
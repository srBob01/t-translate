package ru.arsentiev.translator.handler;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access denied: check your IAM token");
    }
}

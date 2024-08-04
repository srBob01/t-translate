CREATE TABLE IF NOT EXISTS translation_logs
(
    id              SERIAL PRIMARY KEY,
    ip_address      VARCHAR(15)                         NOT NULL,
    input_text      TEXT,
    translated_text TEXT,
    input_lang      TEXT                                NOT NULL,
    translated_lang TEXT                                NOT NULL,
    timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

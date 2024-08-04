package ru.arsentiev.translator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.arsentiev.translator.entity.Response;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseList {
    private List<Response> translations;

    @JsonProperty("translations")
    public List<Response> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Response> translations) {
        this.translations = translations;
    }
}
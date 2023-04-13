package dev.yive.webhook.json.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationResponse {
    private final String id;

    public ValidationResponse(String id) {
        this.id = id;
    }
}

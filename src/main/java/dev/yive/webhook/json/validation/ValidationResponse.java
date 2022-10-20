package dev.yive.webhook.json.validation;

public class ValidationResponse {
    private final String id;

    public ValidationResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

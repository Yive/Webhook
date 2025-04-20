package dev.yive.webhook.json.misc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurringPaymentStatus {
    private int id;
    private String description;
}

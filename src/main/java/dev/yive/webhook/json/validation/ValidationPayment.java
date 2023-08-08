package dev.yive.webhook.json.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.subjects.PaymentSubject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationPayment {
    private String id;
    private String type;
    private String date;
    private PaymentSubject subject;
}

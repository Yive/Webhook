package dev.yive.webhook.json.subjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.products.Price;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurringPaymentSubject {
    private String reference;
    private String created_at;
    private String next_payment_at;
    private PaymentSubject initial_payment;
    private PaymentSubject last_payment;
    private int fail_count;
    private Price price;
    private String cancelled_at;
    private String cancel_reason;
}

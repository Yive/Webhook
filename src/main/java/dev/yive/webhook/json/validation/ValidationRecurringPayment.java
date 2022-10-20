package dev.yive.webhook.json.validation;

import dev.yive.webhook.json.subjects.RecurringPaymentSubject;

public class ValidationRecurringPayment {
    private String id;
    private String type;
    private String date;
    private RecurringPaymentSubject subject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RecurringPaymentSubject getSubject() {
        return subject;
    }

    public void setSubject(RecurringPaymentSubject subject) {
        this.subject = subject;
    }
}

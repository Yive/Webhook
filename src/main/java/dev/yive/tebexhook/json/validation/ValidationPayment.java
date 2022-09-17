package dev.yive.tebexhook.json.validation;

import dev.yive.tebexhook.json.subjects.PaymentSubject;

public class ValidationPayment {
    private String id;
    private String type;
    private String date;
    private PaymentSubject subject;

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

    public PaymentSubject getSubject() {
        return subject;
    }

    public void setSubject(PaymentSubject subject) {
        this.subject = subject;
    }
}

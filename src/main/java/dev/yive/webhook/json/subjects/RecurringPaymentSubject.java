package dev.yive.webhook.json.subjects;

import dev.yive.webhook.json.products.Price;
import dev.yive.webhook.json.misc.Status;

public class RecurringPaymentSubject {
    private String reference;
    private String created_at;
    private String next_payment_at;
    private Status status;
    private PaymentSubject initial_payment;
    private PaymentSubject last_payment;
    private int fail_count;
    private Price price;
    private String cancelled_at;
    private String cancel_reason;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNext_payment_at() {
        return next_payment_at;
    }

    public void setNext_payment_at(String next_payment_at) {
        this.next_payment_at = next_payment_at;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public PaymentSubject getInitial_payment() {
        return initial_payment;
    }

    public void setInitial_payment(PaymentSubject initial_payment) {
        this.initial_payment = initial_payment;
    }

    public PaymentSubject getLast_payment() {
        return last_payment;
    }

    public void setLast_payment(PaymentSubject last_payment) {
        this.last_payment = last_payment;
    }

    public int getFail_count() {
        return fail_count;
    }

    public void setFail_count(int fail_count) {
        this.fail_count = fail_count;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getCancelled_at() {
        return cancelled_at;
    }

    public void setCancelled_at(String cancelled_at) {
        this.cancelled_at = cancelled_at;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }
}

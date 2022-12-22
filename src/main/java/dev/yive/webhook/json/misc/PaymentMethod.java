package dev.yive.webhook.json.misc;

public class PaymentMethod {
    private String name;
    private boolean refundable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRefundable() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }
}

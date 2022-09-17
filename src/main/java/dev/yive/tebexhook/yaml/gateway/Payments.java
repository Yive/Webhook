package dev.yive.tebexhook.yaml.gateway;

public class Payments {
    private Gateway complete;
    private Gateway declined;
    private Gateway refunded;

    public Gateway getComplete() {
        return complete;
    }

    public void setComplete(Gateway complete) {
        this.complete = complete;
    }

    public Gateway getDeclined() {
        return declined;
    }

    public void setDeclined(Gateway declined) {
        this.declined = declined;
    }

    public Gateway getRefunded() {
        return refunded;
    }

    public void setRefunded(Gateway refunded) {
        this.refunded = refunded;
    }
}

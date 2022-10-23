package dev.yive.webhook.json.misc;

import dev.yive.webhook.json.products.Price;

public class Fees {
    private Price tax;
    private Price gateway;

    public Price getTax() {
        return tax;
    }

    public void setTax(Price tax) {
        this.tax = tax;
    }

    public Price getGateway() {
        return gateway;
    }

    public void setGateway(Price gateway) {
        this.gateway = gateway;
    }
}

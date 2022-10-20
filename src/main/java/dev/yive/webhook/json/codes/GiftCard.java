package dev.yive.webhook.json.codes;

import dev.yive.webhook.json.products.Price;

public class GiftCard {
    private String card_number;
    private Price amount;

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public Price getAmount() {
        return amount;
    }

    public void setAmount(Price amount) {
        this.amount = amount;
    }
}

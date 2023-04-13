package dev.yive.webhook.json.subjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.codes.GiftCard;
import dev.yive.webhook.json.customer.Customer;
import dev.yive.webhook.json.misc.Fees;
import dev.yive.webhook.json.products.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSubject {
    private String transaction_id;
    private Fees fees;
    private Customer customer;
    private List<Product> products;
    private List<GiftCard> gift_cards;
}

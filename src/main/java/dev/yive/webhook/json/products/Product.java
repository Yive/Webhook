package dev.yive.webhook.json.products;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.customer.Username;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String name;
    private int quantity;
    private Price paid_price;
    private Username username;
}

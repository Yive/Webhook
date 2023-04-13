package dev.yive.webhook.json.codes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.products.Price;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftCard {
    private Price amount;
}

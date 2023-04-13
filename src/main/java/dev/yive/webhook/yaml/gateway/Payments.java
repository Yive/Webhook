package dev.yive.webhook.yaml.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payments {
    private Gateway complete;
    private Gateway declined;
    private Gateway refunded;
}

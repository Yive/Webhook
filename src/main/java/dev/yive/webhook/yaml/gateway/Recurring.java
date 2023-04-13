package dev.yive.webhook.yaml.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recurring {
    private Gateway ended;
    private Gateway renewed;
    private Gateway started;
}

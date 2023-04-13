package dev.yive.webhook.yaml.gateway;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Disputes {
    private Gateway closed;
    private Gateway lost;
    private Gateway opened;
    private Gateway won;
}

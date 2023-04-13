package dev.yive.webhook.yaml.gateway;

import dev.yive.webhook.yaml.Discord;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gateway {
    private boolean enabled;
    private Discord discord;
}

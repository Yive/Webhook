package dev.yive.webhook.yaml;

import dev.yive.webhook.yaml.gateway.Disputes;
import dev.yive.webhook.yaml.gateway.Payments;
import dev.yive.webhook.yaml.gateway.Recurring;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    private String ip;
    private int port;
    private Spiget spiget;
    private Tebex tebex;
    private Discord discord;
    private Disputes disputes;
    private Payments payments;
    private Recurring recurring;
}

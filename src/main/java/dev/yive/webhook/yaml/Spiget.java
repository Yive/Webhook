package dev.yive.webhook.yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Spiget {
    private String id, secret, discord;
    private Set<String> resources;
}

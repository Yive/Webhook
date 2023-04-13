package dev.yive.webhook.yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Tebex {
    private String key;
    private List<String> ips;
}

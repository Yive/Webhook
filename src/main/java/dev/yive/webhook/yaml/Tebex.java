package dev.yive.webhook.yaml;

import java.util.List;

public class Tebex {
    private String key;
    private List<String> ips;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }
}

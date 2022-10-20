package dev.yive.webhook.yaml.gateway;

import dev.yive.webhook.yaml.Discord;

public class Gateway {
    private boolean enabled;
    private Discord discord;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Discord getDiscord() {
        return discord;
    }

    public void setDiscord(Discord discord) {
        this.discord = discord;
    }
}

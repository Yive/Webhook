package dev.yive.webhook.json.discord;

import dev.yive.webhook.json.discord.component.Components;
import dev.yive.webhook.json.discord.embed.Embed;

import java.util.List;

public class WebhookBody {
    private List<Components> components;
    private List<Embed> embeds;

    public List<Components> getComponents() {
        return components;
    }

    public void setComponents(List<Components> components) {
        this.components = components;
    }

    public List<Embed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
    }
}

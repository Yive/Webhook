package dev.yive.webhook.json.discord;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.yive.webhook.json.discord.embed.Embed;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookBody {
    private List<Embed> embeds;
}

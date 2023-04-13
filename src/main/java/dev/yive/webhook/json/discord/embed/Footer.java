package dev.yive.webhook.json.discord.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Footer {
    private String text;
    private String icon_url;
    private String proxy_icon_url;
}

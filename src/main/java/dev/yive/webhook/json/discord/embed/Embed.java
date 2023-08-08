package dev.yive.webhook.json.discord.embed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Embed {
    private String title;
    private String url;
    private String timestamp;
    private int color;
    private Footer footer;
    private Media thumbnail;
    private Author author;
    private List<Field> fields;
}

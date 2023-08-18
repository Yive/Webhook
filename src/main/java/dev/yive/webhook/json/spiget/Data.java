package dev.yive.webhook.json.spiget;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    private String name;
    private Version version;
    private Icon icon;
    private int id;
    private int updateId;
}

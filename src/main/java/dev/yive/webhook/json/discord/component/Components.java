package dev.yive.webhook.json.discord.component;

import java.util.List;

public class Components {
    private int type;
    private List<Button> components;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Button> getComponents() {
        return components;
    }

    public void setComponents(List<Button> components) {
        this.components = components;
    }
}

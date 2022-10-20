package dev.yive.webhook.yaml;

public class Discord {
    private boolean embeds;
    private String url;

    public boolean isEmbeds() {
        return embeds;
    }

    public void setEmbeds(boolean embeds) {
        this.embeds = embeds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package dev.yive.tebexhook.yaml.gateway;

public class Recurring {
    private Gateway ended;
    private Gateway renewed;
    private Gateway started;

    public Gateway getEnded() {
        return ended;
    }

    public void setEnded(Gateway ended) {
        this.ended = ended;
    }

    public Gateway getRenewed() {
        return renewed;
    }

    public void setRenewed(Gateway renewed) {
        this.renewed = renewed;
    }

    public Gateway getStarted() {
        return started;
    }

    public void setStarted(Gateway started) {
        this.started = started;
    }
}

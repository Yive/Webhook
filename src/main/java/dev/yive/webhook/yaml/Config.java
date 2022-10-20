package dev.yive.webhook.yaml;

import dev.yive.webhook.yaml.gateway.Disputes;
import dev.yive.webhook.yaml.gateway.Payments;
import dev.yive.webhook.yaml.gateway.Recurring;

public class Config {
    private String ip;
    private int port;
    private Spiget spiget;
    private Tebex tebex;
    private Discord discord;
    private Disputes disputes;
    private Payments payments;
    private Recurring recurring;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Spiget getSpiget() {
        return spiget;
    }

    public void setSpiget(Spiget spiget) {
        this.spiget = spiget;
    }

    public Tebex getTebex() {
        return tebex;
    }

    public void setTebex(Tebex tebex) {
        this.tebex = tebex;
    }

    public Discord getDiscord() {
        return discord;
    }

    public void setDiscord(Discord discord) {
        this.discord = discord;
    }

    public Disputes getDisputes() {
        return disputes;
    }

    public void setDisputes(Disputes disputes) {
        this.disputes = disputes;
    }

    public Payments getPayments() {
        return payments;
    }

    public void setPayments(Payments payments) {
        this.payments = payments;
    }

    public Recurring getRecurring() {
        return recurring;
    }

    public void setRecurring(Recurring recurring) {
        this.recurring = recurring;
    }
}

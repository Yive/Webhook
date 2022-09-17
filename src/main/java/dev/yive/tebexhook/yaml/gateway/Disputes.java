package dev.yive.tebexhook.yaml.gateway;

public class Disputes {
    private Gateway closed;
    private Gateway lost;
    private Gateway opened;
    private Gateway won;

    public Gateway getClosed() {
        return closed;
    }

    public void setClosed(Gateway closed) {
        this.closed = closed;
    }

    public Gateway getLost() {
        return lost;
    }

    public void setLost(Gateway lost) {
        this.lost = lost;
    }

    public Gateway getOpened() {
        return opened;
    }

    public void setOpened(Gateway opened) {
        this.opened = opened;
    }

    public Gateway getWon() {
        return won;
    }

    public void setWon(Gateway won) {
        this.won = won;
    }
}

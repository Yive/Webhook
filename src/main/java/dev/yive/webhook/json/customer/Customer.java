package dev.yive.webhook.json.customer;

public class Customer {
    private String first_name;
    private String last_name;
    private String email;
    private String ip;
    private Username username;
    private boolean marketing_consent;
    private String country;
    private String postal_code;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }

    public boolean isMarketing_consent() {
        return marketing_consent;
    }

    public void setMarketing_consent(boolean marketing_consent) {
        this.marketing_consent = marketing_consent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }
}

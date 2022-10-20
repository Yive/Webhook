package dev.yive.webhook.json.products;

import dev.yive.webhook.json.misc.Variable;
import dev.yive.webhook.json.customer.Username;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private int quantity;
    private Price base_price;
    private Price paid_price;
    private List<Variable> variables;
    private String expires_at;
    private String custom;
    private Username username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Price getBase_price() {
        return base_price;
    }

    public void setBase_price(Price base_price) {
        this.base_price = base_price;
    }

    public Price getPaid_price() {
        return paid_price;
    }

    public void setPaid_price(Price paid_price) {
        this.paid_price = paid_price;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }
}

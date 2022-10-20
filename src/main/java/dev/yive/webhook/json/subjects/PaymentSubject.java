package dev.yive.webhook.json.subjects;

import dev.yive.webhook.json.customer.Customer;
import dev.yive.webhook.json.products.Price;
import dev.yive.webhook.json.products.Product;
import dev.yive.webhook.json.misc.Status;
import dev.yive.webhook.json.codes.GiftCard;

import java.util.List;

public class PaymentSubject {
    private String transaction_id;
    private Status status;
    private String payment_sequence;
    private String created_at;
    private Price price;
    private List<Price> fees;
    private Customer customer;
    private List<Product> products;
    private List<Object> coupons;
    private List<GiftCard> gift_cards;
    private String recurring_payment_reference;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPayment_sequence() {
        return payment_sequence;
    }

    public void setPayment_sequence(String payment_sequence) {
        this.payment_sequence = payment_sequence;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public List<Price> getFees() {
        return fees;
    }

    public void setFees(List<Price> fees) {
        this.fees = fees;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Object> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Object> coupons) {
        this.coupons = coupons;
    }

    public List<GiftCard> getGift_cards() {
        return gift_cards;
    }

    public void setGift_cards(List<GiftCard> gift_cards) {
        this.gift_cards = gift_cards;
    }

    public String getRecurring_payment_reference() {
        return recurring_payment_reference;
    }

    public void setRecurring_payment_reference(String recurring_payment_reference) {
        this.recurring_payment_reference = recurring_payment_reference;
    }
}

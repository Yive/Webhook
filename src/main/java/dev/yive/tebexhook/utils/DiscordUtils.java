package dev.yive.tebexhook.utils;

import dev.yive.tebexhook.json.codes.GiftCard;
import dev.yive.tebexhook.json.customer.Username;
import dev.yive.tebexhook.json.discord.component.Button;
import dev.yive.tebexhook.json.discord.component.Components;
import dev.yive.tebexhook.json.discord.embed.Embed;
import dev.yive.tebexhook.json.discord.embed.Field;
import dev.yive.tebexhook.json.discord.embed.Footer;
import dev.yive.tebexhook.json.products.Product;
import dev.yive.tebexhook.json.subjects.PaymentSubject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DiscordUtils {
    public static Embed createEmbed(PaymentSubject subject) {
        Embed embed = new Embed();
        embed.setFields(new ArrayList<>());

        double paidPrice = 0;
        double giftCardsPrice = 0;

        StringBuilder packagesBuilder = new StringBuilder("```\n");
        for (Product product : subject.getProducts()) {
            paidPrice = paidPrice + product.getPaid_price().getAmount();
            packagesBuilder
                    .append("[")
                    .append(product.getQuantity())
                    .append("x] ")
                    .append(product.getName())
                    .append("\n");
        }
        packagesBuilder.append("```");

        Field field = new Field();
        field.setName("Packages");
        field.setValue(packagesBuilder.toString());
        embed.setFields(Collections.singletonList(field));

        for (GiftCard card : subject.getGift_cards()) {
            giftCardsPrice = giftCardsPrice + card.getAmount().getAmount();
        }
        double original = paidPrice;
        paidPrice = paidPrice - (original * 0.0247);
        paidPrice = paidPrice - (original * 0.05);
        paidPrice = Math.max(0, paidPrice - giftCardsPrice);

        Username username = subject.getCustomer().getUsername();
        Footer footer = new Footer();
        footer.setText("Revenue: $" + String.format("%.2f", Math.max(0, paidPrice)) + "\nBuyer IGN: " + username.getUsername() + "\nBuyer UUID: " + username.getId() + "\nID: " + subject.getTransaction_id());
        footer.setIcon_url("https://crafthead.net/helm/" + username.getId() + ".png");
        embed.setFooter(footer);

        return embed;
    }

    public static Components createComponents(PaymentSubject subject) {
        Components components = new Components();
        components.setType(1);

        Button transaction = new Button();
        transaction.setType(2);
        transaction.setStyle(5);
        transaction.setDisabled(false);
        transaction.setLabel("View Transaction");
        transaction.setUrl("https://server.tebex.io/search/" + subject.getTransaction_id() + "/payments");

        Button buyer = new Button();
        buyer.setType(2);
        buyer.setStyle(5);
        buyer.setDisabled(false);
        buyer.setLabel("View Buyer");
        buyer.setUrl("https://server.tebex.io/lookup/" + subject.getCustomer().getUsername().getId());

        components.setComponents(Arrays.asList(transaction, buyer));
        return components;
    }
}

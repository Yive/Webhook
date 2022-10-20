package dev.yive.webhook.utils;

import dev.yive.webhook.json.codes.GiftCard;
import dev.yive.webhook.json.customer.Username;
import dev.yive.webhook.json.discord.component.Button;
import dev.yive.webhook.json.discord.component.Components;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.discord.embed.Field;
import dev.yive.webhook.json.discord.embed.Footer;
import dev.yive.webhook.json.discord.embed.Media;
import dev.yive.webhook.json.products.Product;
import dev.yive.webhook.json.spiget.Data;
import dev.yive.webhook.json.spiget.ResourceUpdate;
import dev.yive.webhook.json.spiget.Version;
import dev.yive.webhook.json.subjects.PaymentSubject;

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
        footer.setText("Revenue: $" + String.format("%.2f", Math.max(0, paidPrice)) + "\nBuyer IGN: " + username.getUsername() + "\nBuyer UUID: " + username.getId() + "\nID: " + subject.getTransaction_id() + "\nWebhook v1.1.0");
        footer.setIcon_url("https://crafthead.net/helm/" + username.getId() + ".png");
        embed.setFooter(footer);

        return embed;
    }

    public static Embed createEmbed(ResourceUpdate update) {
        Embed embed = new Embed();
        embed.setFields(new ArrayList<>());

        Data data = update.getData();
        embed.setTitle(data.getName());
        embed.setDescription("Update detected");
        embed.setUrl("https://www.spigotmc.org/resources/" + data.getId() + "/update?update=" + data.getUpdateId());

        Media media = new Media();
        String url = data.getIcon().getUrl();
        media.setUrl(url == null || url.isEmpty() ? "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png" : "https://www.spigotmc.org" + url);
        embed.setThumbnail(media);

        Field field = new Field();
        field.setName("Version");
        field.setValue(data.getVersion().getName());
        embed.setFields(Collections.singletonList(field));

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
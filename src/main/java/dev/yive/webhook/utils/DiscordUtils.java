package dev.yive.webhook.utils;

import dev.yive.webhook.json.codes.GiftCard;
import dev.yive.webhook.json.customer.Username;
import dev.yive.webhook.json.discord.embed.Author;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.discord.embed.Field;
import dev.yive.webhook.json.discord.embed.Footer;
import dev.yive.webhook.json.discord.embed.Media;
import dev.yive.webhook.json.misc.Fees;
import dev.yive.webhook.json.products.Product;
import dev.yive.webhook.json.spiget.Data;
import dev.yive.webhook.json.spiget.ResourceUpdate;
import dev.yive.webhook.json.subjects.PaymentSubject;
import dev.yive.webhook.json.validation.ValidationPayment;

import java.util.ArrayList;
import java.util.Collections;

public class DiscordUtils {
    public static Embed createEmbed(String authorName, ValidationPayment payment, PaymentSubject subject) {
        Embed embed = new Embed();
        embed.setTimestamp(payment.getDate());

        Author author = new Author();
        author.setName(authorName);
        author.setUrl("https://creator.tebex.io/search/" + subject.getTransaction_id() + "/payments");
        embed.setAuthor(author);

        Username username = subject.getCustomer().getUsername();
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(createField("Transaction ID", subject.getTransaction_id(), true));
        fields.add(createField("Payment Method", subject.getPayment_method().getName(), true));
        fields.add(createField("Buyer Username", username.getUsername(), false));
        fields.add(createField("Buyer UUID", username.getId(), false));

        double paidPrice = 0;
        double giftCardsPrice = 0;
        StringBuilder packagesBuilder = new StringBuilder();
        for (Product product : subject.getProducts()) {
            paidPrice = paidPrice + product.getPaid_price().getAmount();
            packagesBuilder.append("[").append(product.getQuantity()).append("x] ").append(product.getName()).append("\n");
        }

        for (GiftCard card : subject.getGift_cards()) {
            giftCardsPrice = giftCardsPrice + card.getAmount().getAmount();
        }

        Fees fees = subject.getFees();
        paidPrice = paidPrice - fees.getTax().getAmount();
        paidPrice = paidPrice - fees.getGateway().getAmount();
        paidPrice = Math.max(0, paidPrice - giftCardsPrice);

        fields.add(createField("Packages [" + String.format("%.2f", paidPrice) + "]", packagesBuilder.toString(), false));
        embed.setFields(fields);

        Footer footer = new Footer();
        footer.setText("v1.1.8");
        embed.setFooter(footer);

        Media thumbnail = new Media();
        thumbnail.setUrl("https://crafthead.net/helm/" + username.getId() + "/64.png");
        embed.setThumbnail(thumbnail);

        return embed;
    }

    public static Embed createEmbed(ResourceUpdate update) {
        Embed embed = new Embed();
        embed.setFields(new ArrayList<>());

        Data data = update.getData();
        embed.setTitle(data.getName());
        embed.setUrl("https://www.spigotmc.org/resources/" + data.getId() + "/update?update=" + data.getUpdateId());
        embed.setColor(convertColour(255, 222, 0));

        Media media = new Media();
        String url = data.getIcon().getUrl();
        media.setUrl(url == null || url.isEmpty() ? "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png" : "https://www.spigotmc.org/" + url);
        embed.setThumbnail(media);

        Field field = new Field();
        field.setName("Version");
        field.setValue(data.getVersion().getName());

        Footer footer = new Footer();
        footer.setText("Webhook v1.1.7");
        embed.setFooter(footer);

        embed.setFields(Collections.singletonList(field));
        return embed;
    }

    public static int convertColour(int r, int g, int b) {
        return ((r & 0x0ff) <<16) | ((g & 0x0ff) <<8) | (b & 0x0ff);
    }

    private static Field createField(String title, String value, boolean inline) {
        Field field = new Field();
        field.setName(title);
        field.setValue("```\n" + value + "\n```");
        field.setInline(inline);
        return field;
    }
}

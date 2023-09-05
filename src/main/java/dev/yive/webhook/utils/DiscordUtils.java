package dev.yive.webhook.utils;

import com.jakewharton.fliptables.FlipTable;
import dev.yive.webhook.Main;
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
import java.util.List;

public class DiscordUtils {
    public static double getRevenue(PaymentSubject subject) {
        double paidPrice = subject.getPrice_paid().getAmount();
        double giftCardsPrice = 0;
        if (paidPrice > 0) {
            // TODO: Use the platform fee whenever it is added to the webhook.
            paidPrice = paidPrice - (paidPrice * 0.05);
            for (GiftCard card : subject.getGift_cards()) {
                giftCardsPrice = giftCardsPrice + card.getAmount().getAmount();
            }
        }

        Fees fees = subject.getFees();
        paidPrice = paidPrice - fees.getTax().getAmount();
        paidPrice = paidPrice - fees.getGateway().getAmount();
        return Math.max(0, paidPrice - giftCardsPrice);
    }

    public static Embed createEmbed(String authorName, ValidationPayment payment, PaymentSubject subject, double revenue) {
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
        fields.add(createField("Buyer", username.getId() + " | " + username.getUsername(), false));

        List<Product> products = subject.getProducts();
        String[][] rows = new String[products.size()][3];
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            rows[i][0] = String.valueOf(product.getQuantity());
            rows[i][1] = product.getName();
            rows[i][2] = product.getUsername().getUsername();
        }

        fields.add(createField("Packages [$" + String.format("%.2f", revenue) + "]", FlipTable.of(new String[]{"Count", "Package", "IGN"}, rows), false));

        if (subject.getDecline_reason() != null) {
            fields.add(createField("Denied Code", subject.getDecline_reason().getCode(), false));
            fields.add(createField("Denied Reason", subject.getDecline_reason().getMessage().replace(" - transaction ID: " + subject.getTransaction_id(), ""), false));
        }

        embed.setFields(fields);

        Footer footer = new Footer();
        footer.setText(Main.VERSION);
        embed.setFooter(footer);

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
        footer.setText(Main.VERSION);
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

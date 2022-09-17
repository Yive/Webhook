package dev.yive.tebexhook.handlers.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.tebexhook.Main;
import dev.yive.tebexhook.json.codes.GiftCard;
import dev.yive.tebexhook.json.discord.WebhookBody;
import dev.yive.tebexhook.json.discord.embed.Embed;
import dev.yive.tebexhook.json.products.Product;
import dev.yive.tebexhook.json.subjects.PaymentSubject;
import dev.yive.tebexhook.json.validation.ValidationPayment;
import dev.yive.tebexhook.json.validation.ValidationResponse;
import dev.yive.tebexhook.utils.DiscordUtils;
import dev.yive.tebexhook.utils.WebUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpCode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;

public class PaymentRefundedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getPayments().getRefunded().isEnabled()) return;
        ValidationPayment payment = ctx.bodyAsClass(ValidationPayment.class);
        ctx.status(HttpCode.OK);
        ctx.json(new ValidationResponse(payment.getId()));
        if (payment.getType().equals("validation.webhook")) return;

        PaymentSubject subject = payment.getSubject();
        String url = Main.config.getPayments().getRefunded().getDiscord().getUrl();
        Embed embed = DiscordUtils.createEmbed(subject);
        embed.setTitle("Payment Refunded");
        embed.setUrl("https://server.tebex.io/search/" + subject.getTransaction_id() + "/payments");
        double paidPrice = 0;
        double giftCardsPrice = 0;

        for (Product product : subject.getProducts()) {
            paidPrice = paidPrice + product.getPaid_price().getAmount();
        }
        for (GiftCard card : subject.getGift_cards()) {
            giftCardsPrice = giftCardsPrice + card.getAmount().getAmount();
        }
        double original = paidPrice;
        paidPrice = paidPrice - (original * 0.0247);
        paidPrice = paidPrice - (original * 0.05);
        embed.setColor(Math.max(0, Math.max(0, paidPrice - giftCardsPrice)) > 0 ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
        WebhookBody body = new WebhookBody();
        //body.setComponents(Collections.singletonList(DiscordUtils.createComponents(subject)));
        body.setEmbeds(Collections.singletonList(embed));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebUtil.sendRequest(url.isEmpty() ? Main.config.getDiscord().getUrl() : url, mapper.writeValueAsString(body));
    }
}

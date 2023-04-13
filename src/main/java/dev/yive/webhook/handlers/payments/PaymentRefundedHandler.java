package dev.yive.webhook.handlers.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.webhook.Main;
import dev.yive.webhook.json.codes.GiftCard;
import dev.yive.webhook.json.discord.WebhookBody;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.misc.Fees;
import dev.yive.webhook.json.products.Product;
import dev.yive.webhook.json.subjects.PaymentSubject;
import dev.yive.webhook.json.validation.ValidationPayment;
import dev.yive.webhook.json.validation.ValidationResponse;
import dev.yive.webhook.utils.DiscordUtils;
import dev.yive.webhook.utils.WebUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Collections;

public class PaymentRefundedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getPayments().getRefunded().isEnabled()) return;
        ValidationPayment payment = ctx.bodyAsClass(ValidationPayment.class);
        ctx.status(HttpStatus.OK);
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
        Fees fees = subject.getFees();
        paidPrice = paidPrice - fees.getTax().getAmount();
        paidPrice = paidPrice - fees.getGateway().getAmount();
        embed.setColor(Math.round(Math.max(0, Math.max(0, paidPrice - giftCardsPrice)) * 100.0) / 100.0 > 0 ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
        WebhookBody body = new WebhookBody();
        body.setEmbeds(Collections.singletonList(embed));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebUtil.sendRequest(url.isEmpty() ? Main.config.getDiscord().getUrl() : url, mapper.writeValueAsString(body));
    }
}

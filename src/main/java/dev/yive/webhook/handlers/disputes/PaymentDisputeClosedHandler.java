package dev.yive.webhook.handlers.disputes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.webhook.Main;
import dev.yive.webhook.json.codes.GiftCard;
import dev.yive.webhook.json.discord.WebhookBody;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.products.Price;
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

public class PaymentDisputeClosedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getDisputes().getClosed().isEnabled()) return;
        ValidationPayment payment = ctx.bodyAsClass(ValidationPayment.class);
        ctx.status(HttpStatus.OK);
        ctx.json(new ValidationResponse(payment.getId()));
        if (payment.getType().equals("validation.webhook")) return;

        PaymentSubject subject = payment.getSubject();
        String url = Main.config.getDisputes().getClosed().getDiscord().getUrl();
        Embed embed = DiscordUtils.createEmbed(subject);
        embed.setTitle("Chargeback Closed");
        embed.setUrl("https://server.tebex.io/search/" + subject.getTransaction_id() + "/payments");
        double paidPrice = 0;
        double giftCardsPrice = 0;

        for (Product product : subject.getProducts()) {
            paidPrice = paidPrice + product.getPaid_price().getAmount();
        }
        for (GiftCard card : subject.getGift_cards()) {
            giftCardsPrice = giftCardsPrice + card.getAmount().getAmount();
        }
        for (Price fee : subject.getFees()) {
            paidPrice = paidPrice - fee.getAmount();
        }
        embed.setColor(Math.round(Math.max(0, Math.max(0, paidPrice - giftCardsPrice)) * 100.0) / 100.0 > 0 ? new Color(0, 128, 0).getRGB() : Color.GRAY.getRGB());
        WebhookBody body = new WebhookBody();
        //body.setComponents(Collections.singletonList(DiscordUtils.createComponents(subject)));
        body.setEmbeds(Collections.singletonList(embed));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebUtil.sendRequest(url.isEmpty() ? Main.config.getDiscord().getUrl() : url, mapper.writeValueAsString(body));
    }
}

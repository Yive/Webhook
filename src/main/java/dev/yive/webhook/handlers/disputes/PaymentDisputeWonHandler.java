package dev.yive.webhook.handlers.disputes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.webhook.Main;
import dev.yive.webhook.json.discord.WebhookBody;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.subjects.PaymentSubject;
import dev.yive.webhook.json.validation.ValidationPayment;
import dev.yive.webhook.json.validation.ValidationResponse;
import dev.yive.webhook.utils.DiscordUtils;
import dev.yive.webhook.utils.WebUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class PaymentDisputeWonHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getDisputes().getWon().isEnabled()) return;
        ValidationPayment payment = ctx.bodyAsClass(ValidationPayment.class);
        ctx.status(HttpStatus.OK);
        ctx.json(new ValidationResponse(payment.getId()));
        if (payment.getType().equals("validation.webhook")) return;

        PaymentSubject subject = payment.getSubject();
        double revenue = DiscordUtils.getRevenue(subject);
        String url = Main.config.getDisputes().getWon().getDiscord().getUrl();
        Embed embed = DiscordUtils.createEmbed("Chargeback Won", payment, subject, revenue);

        embed.setColor(Math.round(Math.max(0, revenue) * 100.0) / 100.0 > 0 ? DiscordUtils.convertColour(0, 255, 0) : DiscordUtils.convertColour(128, 128, 128));
        WebhookBody body = new WebhookBody();
        body.setEmbeds(Collections.singletonList(embed));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebUtil.sendRequest(url.isEmpty() ? Main.config.getDiscord().getUrl() : url, mapper.writeValueAsString(body));
    }
}

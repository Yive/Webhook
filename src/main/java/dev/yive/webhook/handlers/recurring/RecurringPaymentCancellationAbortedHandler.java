package dev.yive.webhook.handlers.recurring;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.webhook.Main;
import dev.yive.webhook.json.discord.WebhookBody;
import dev.yive.webhook.json.discord.embed.Embed;
import dev.yive.webhook.json.subjects.RecurringPaymentSubject;
import dev.yive.webhook.json.validation.ValidationRecurringPayment;
import dev.yive.webhook.json.validation.ValidationResponse;
import dev.yive.webhook.utils.DiscordUtils;
import dev.yive.webhook.utils.WebUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class RecurringPaymentCancellationAbortedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getRecurring().getCancellationAborted().isEnabled()) return;
        ValidationRecurringPayment payment = ctx.bodyAsClass(ValidationRecurringPayment.class);
        ctx.json(new ValidationResponse(payment.getId()));
        if (payment.getType().equals("validation.webhook")) return;

        RecurringPaymentSubject subject = payment.getSubject();
        double revenue = DiscordUtils.getRevenue(subject);
        String url = Main.config.getRecurring().getCancellationAborted().getDiscord().getUrl();
        Embed embed = DiscordUtils.createEmbed("Recurring Payment Cancellation Aborted", payment, subject, revenue);

        embed.setColor(Math.round(Math.max(0, revenue) * 100.0) / 100.0 > 0 ? DiscordUtils.convertColour(0, 255, 0) : DiscordUtils.convertColour(128, 128, 128));
        WebhookBody body = new WebhookBody();
        body.setEmbeds(Collections.singletonList(embed));

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        WebUtil.sendRequest(url.isEmpty() ? Main.config.getDiscord().getUrl() : url, mapper.writeValueAsString(body));
    }
}

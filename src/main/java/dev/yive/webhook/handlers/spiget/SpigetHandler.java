package dev.yive.webhook.handlers.spiget;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.yive.webhook.Main;
import dev.yive.webhook.json.discord.WebhookBody;
import dev.yive.webhook.json.spiget.ResourceUpdate;
import dev.yive.webhook.utils.DiscordUtils;
import dev.yive.webhook.utils.WebUtil;
import dev.yive.webhook.yaml.Spiget;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.util.JavalinLogger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SpigetHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        try {
            ResourceUpdate update = ctx.bodyAsClass(ResourceUpdate.class);
            Spiget spiget = Main.config.getSpiget();
            if (!spiget.getResources().contains(Integer.toString(update.getData().getId())) || spiget.getDiscord() == null || spiget.getDiscord().isEmpty()) return;
            WebhookBody body = new WebhookBody();
            body.setEmbeds(Collections.singletonList(DiscordUtils.createEmbed(update)));

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            WebUtil.sendRequest(spiget.getDiscord(), mapper.writeValueAsString(body));
        } catch (Exception e) {
            JavalinLogger.error("Error while handling Spiget webhook", e);
            JavalinLogger.info(ctx.body());
        }
    }
}

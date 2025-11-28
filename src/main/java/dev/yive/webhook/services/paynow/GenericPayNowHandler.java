package dev.yive.webhook.services.paynow;

import dev.yive.webhook.MainVerticle;
import dev.yive.webhook.utils.DiscordUtils;
import dev.yive.webhook.utils.PayNowUtils;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface GenericPayNowHandler extends Handler<RoutingContext> {
  Logger LOGGER = Logger.getLogger("PayNow");
  @Override
  default void handle(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    JsonObject responseJson = new JsonObject().put("event_id", body.getString("event_id"));

    ConfigRetriever config = MainVerticle.CONFIGS.get(config());
    if (config == null || !config.getCachedConfig().getBoolean("enabled", false)) {
      ctx.json(responseJson);
      return;
    }

    String discordURL = config.getCachedConfig().getString("discord");
    if (discordURL == null || discordURL.isEmpty()) {
      ctx.json(responseJson);
      return;
    }

    ConfigRetriever discord = MainVerticle.CONFIGS.get(discord());
    if (discord == null) {
      ctx.json(responseJson);
      return;
    }

    JsonObject discordJson = discord.getCachedConfig();
    if (discordJson.isEmpty()) {
      ctx.json(responseJson);
      return;
    }

    JsonObject parsed = PayNowUtils.parse(body, discordJson);
    if (parsed == null || parsed.isEmpty()) {
      ctx.json(responseJson);
      return;
    }

    Object transactionId = Optional.ofNullable(DiscordUtils.getNested("body.id", body))
      .orElse(DiscordUtils.getNested("body.subscription_id", body));

    MainVerticle.getInstance().getWebClient()
      .postAbs(discordURL)
      .sendJsonObject(parsed)
      .onSuccess(response -> LOGGER.log(Level.INFO,
        "Successfully sent webhook message for: " +
          transactionId
      ))
      .onFailure(throwable -> LOGGER.log(Level.WARNING,
        "Failed to send webhook message for: " +
          transactionId
        , throwable));
    ctx.json(responseJson);
  }

  String config();

  String discord();
}

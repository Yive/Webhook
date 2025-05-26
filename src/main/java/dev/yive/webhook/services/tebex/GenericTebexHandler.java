package dev.yive.webhook.services.tebex;

import dev.yive.webhook.MainVerticle;
import dev.yive.webhook.utils.DiscordUtils;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface GenericTebexHandler extends Handler<RoutingContext> {
  Logger LOGGER = Logger.getLogger("Tebex");
  @Override
  default void handle(RoutingContext ctx) {
    ConfigRetriever config = MainVerticle.CONFIGS.get(config());
    if (config == null || !config.getCachedConfig().getBoolean("enabled", false)) return;

    String discordURL = config.getCachedConfig().getString("discord");
    if (discordURL == null || discordURL.isEmpty()) return;

    JsonObject body = ctx.body().asJsonObject();
    ctx.json(new JsonObject().put("id", body.getString("id")));
    if (body.getString("type", "").equals("validation.webhook")) return;

    ConfigRetriever discord = MainVerticle.CONFIGS.get(discord());
    if (discord == null) return;

    JsonObject discordJson = discord.getCachedConfig();
    if (discordJson.isEmpty()) return;

    JsonObject parsed = DiscordUtils.parse(body, discordJson);
    if (parsed.isEmpty()) return;

    MainVerticle.getInstance().getWebClient()
      .postAbs(discordURL)
      .sendJsonObject(parsed)
      .onSuccess(response -> {
        LOGGER.log(Level.INFO,
          "Successfully sent webhook message for: " +
            Optional.ofNullable(body.getString("transaction_id"))
              .orElse(body.getString("last_payment.transaction_id"))
        );
      })
      .onFailure(response -> {
        LOGGER.log(Level.WARNING,
          "Failed to send webhook message for: " +
            Optional.ofNullable(body.getString("transaction_id"))
              .orElse(body.getString("last_payment.transaction_id"))
          , response);
      });
  }

  String config();

  String discord();
}

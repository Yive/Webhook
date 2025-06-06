package dev.yive.webhook.services.spiget;

import dev.yive.webhook.MainVerticle;
import dev.yive.webhook.utils.DiscordUtils;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Find out what Spiget sends to webhooks. It has been years since I last used Spiget.
public class SpigetHandler implements Handler<RoutingContext> {
  private static final Logger LOGGER = Logger.getLogger("Spiget");
  @Override
  public void handle(RoutingContext ctx) {
    // Verify if the request has the Spiget header
    String requestId = ctx.request().headers().get("X-Spiget-HookId");
    if (requestId == null) {
      ctx.fail(HttpResponseStatus.UNAUTHORIZED.code());
      return;
    }

    // Attempt to get the config
    ConfigRetriever retriever = MainVerticle.CONFIGS.get("services/other/spiget.yml");
    if (retriever == null) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    JsonObject config = retriever.getCachedConfig();
    if (config.isEmpty()) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    // Attempt to get the id we have in our config
    String id = config.getString("id");
    if (id == null || id.isEmpty()) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    // Fail if the ID doesn't match what is in our config
    if (!id.equals(requestId)) {
      ctx.fail(HttpResponseStatus.UNAUTHORIZED.code());
      return;
    }

    // Attempt to get the list of resources we care about.
    JsonArray resources = config.getJsonArray("resources");
    if (resources == null || resources.isEmpty()) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    String discordURL = config.getString("discord");
    if (discordURL == null || discordURL.isEmpty()) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    try {
      // Turn the body into a json object, makes things easier.
      JsonObject body = ctx.body().asJsonObject();

      // Attempt to get the resource id from the body
      Integer resourceId = body.getInteger("id");
      if (resourceId == null) {
        ctx.fail(HttpResponseStatus.BAD_REQUEST.code());
        return;
      }

      // Exit early if the resource isn't one we care about
      if (!resources.contains(resourceId)) return;

      ConfigRetriever discord = MainVerticle.CONFIGS.get("services/other/spiget.json");
      if (discord == null) return;

      JsonObject discordJson = discord.getCachedConfig();
      if (discordJson.isEmpty()) return;

      JsonObject parsed = DiscordUtils.parse(body, discordJson);
      if (parsed == null || parsed.isEmpty()) return;

      MainVerticle.getInstance().getWebClient()
        .postAbs(discordURL)
        .sendJsonObject(parsed)
        .onSuccess(response -> {
          LOGGER.log(Level.INFO, "Successfully sent webhook message for: " + body.getString("name"));
        })
        .onFailure(response -> {
          LOGGER.log(Level.WARNING, "Failed to send webhook message for: " + body.getString("name"), response);
        });
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, "Error occurred whilst handling a request from: " + ctx.request().remoteAddress(), e);
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    }
  }
}

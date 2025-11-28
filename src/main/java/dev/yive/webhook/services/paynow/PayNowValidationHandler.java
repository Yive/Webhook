package dev.yive.webhook.services.paynow;

import dev.yive.webhook.MainVerticle;
import dev.yive.webhook.utils.CryptoUtils;
import dev.yive.webhook.utils.Pair;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PayNowValidationHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    HttpServerRequest request = ctx.request();
    MultiMap headers = request.headers();
    String ip = Optional.ofNullable(headers.get("CF-Connecting-IP")).orElse(request.remoteAddress().hostAddress());

    // This just allows for development testing.
    if (MainVerticle.VERSION.endsWith("SNAPSHOT") && ip.equals("127.0.0.1")) {
      ctx.next();
      return;
    }

    ConfigRetriever config = MainVerticle.CONFIGS.get("services/paynow/paynow.yml");
    if (config == null) {
      ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      ctx.json(new JsonObject(Map.of("error", "Misconfigured application")));
      return;
    }

    JsonArray ips = config.getCachedConfig().getJsonArray("ips");
    if (ips == null) {
      ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      ctx.json(new JsonObject(Map.of("error", "Misconfigured whitelist")));
      return;
    }

    if (!ips.contains(ip)) {
      ctx.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
      ctx.json(new JsonObject(Map.of("error", "Whitelist failure")));
      return;
    }

    String signature = headers.get("PayNow-Signature");
    if (signature == null) {
      ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
      ctx.json(new JsonObject(Map.of("error", "Missing signature")));
      return;
    }

    String timestamp = headers.get("PayNow-Timestamp");
    if (timestamp == null) {
      ctx.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
      ctx.json(new JsonObject(Map.of("error", "Missing timestamp")));
      return;
    }

    JsonArray keys = config.getCachedConfig().getJsonArray("keys");
    if (keys == null) {
      ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      ctx.json(new JsonObject(Map.of("error", "Misconfigured keys")));
      return;
    }

    byte[] signatureBytes = signature.getBytes(StandardCharsets.UTF_8);
    byte[] dataBytes = (timestamp + "." + ctx.body().asString()).getBytes(StandardCharsets.UTF_8);
    Map<String, Pair<Boolean, byte[]>> results = new HashMap<>(keys.size());
    for (int index = 0; index < keys.size(); index++) {
      String key = keys.getString(index);
      if (key == null) continue;

      try {
        byte[] hex = CryptoUtils.hmac(
          "HmacSHA256",
          dataBytes,
          key.getBytes(StandardCharsets.UTF_8)
        );
        results.put(key, new Pair<>(MessageDigest.isEqual(signatureBytes, Base64.getEncoder().encode(hex)), hex));
      } catch (Exception ignored) {}
    }

    boolean noneMatch = true;
    for (Pair<Boolean, byte[]> pair : results.values()) {
      if (!pair.left()) {
        continue;
      }

      noneMatch = false;
      break;
    }

    if (!noneMatch) {
      ctx.next();
      return;
    }

    ctx.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
    ctx.json(new JsonObject(Map.of("error", "Signature failure")));
  }
}

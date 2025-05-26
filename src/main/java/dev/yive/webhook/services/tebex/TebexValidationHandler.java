package dev.yive.webhook.services.tebex;

import dev.yive.webhook.MainVerticle;
import dev.yive.webhook.utils.CryptoUtils;
import dev.yive.webhook.utils.Pair;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TebexValidationHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext ctx) {
    HttpServerRequest request = ctx.request();
    MultiMap headers = request.headers();
    String ip = Optional.ofNullable(headers.get("CF-Connecting-IP")).orElse(request.remoteAddress().hostAddress());

    // This just allows for development testing.
    if (MainVerticle.VERSION.endsWith("SNAPSHOT") && ip.equals("127.0.0.1")) return;

    ConfigRetriever config = MainVerticle.CONFIGS.get("services/tebex/tebex.yml");
    if (config == null) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    JsonArray ips = config.getCachedConfig().getJsonArray("ips");
    if (ips == null) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    if (!ips.contains(ip)) {
      ctx.fail(HttpResponseStatus.UNAUTHORIZED.code());
      return;
    }

    String signature = headers.get("X-Signature");
    if (signature == null) {
      ctx.fail(HttpResponseStatus.BAD_REQUEST.code());
      return;
    }

    JsonArray keys = config.getCachedConfig().getJsonArray("keys");
    if (keys == null) {
      ctx.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
      return;
    }

    Map<String, Pair<Boolean, String>> results = new HashMap<>(keys.size());
    for (int index = 0; index < keys.size(); index++) {
      String key = keys.getString(index);
      if (key == null) continue;

      try {
        String hex = CryptoUtils.hmac("HmacSHA256",
          CryptoUtils.bytesToHex(
            MessageDigest.getInstance("SHA-256")
              .digest(ctx.body().asString().getBytes(StandardCharsets.UTF_8))
          ),
          key
        );
        results.put(key, new Pair<>(signature.equals(hex), hex));
      } catch (Exception ignored) {}
    }

    boolean noneMatch = true;
    for (Pair<Boolean, String> pair : results.values()) {
      if (!pair.left()) continue;

      noneMatch = false;
      break;
    }

    if (!noneMatch) {
      ctx.next();
      return;
    }

    ctx.fail(HttpResponseStatus.UNAUTHORIZED.code());
  }
}

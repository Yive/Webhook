package dev.yive.webhook.utils;

import com.jakewharton.fliptables.FlipTable;
import dev.yive.webhook.MainVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public class PayNowUtils {
  public static JsonObject parse(JsonObject base, JsonObject discord) {
    JsonObject body = base.getJsonObject("body");
    String encoded = discord.encode();

    // PayNow doesn't provide platform or gateway fees in the request body so the internal revenue tag is ignored
    // TODO: Maybe add support for doing API lookups via the order ID
    /*
    double revenue = getRevenue(body);
    encoded = encoded.replace("<internal:revenue>", String.format("%.2f", revenue));
    */
    encoded = encoded.replace("<internal:type>", body.getString("status", "unknown"));
    encoded = encoded.replace("<internal:version>", MainVerticle.VERSION);
    encoded = encoded.replace("<internal:packages_table>", getPackagesTable(body));

    for (Pattern pattern : DiscordUtils.PATTERNS) {
      encoded = pattern.matcher(encoded).replaceAll(match -> String.valueOf(DiscordUtils.getNested(match.group(2), base)));
    }

    JsonObject discordResponse = Buffer.buffer(encoded).toJsonObject();
    replaceColour(base, body.getInteger("total_amount", 1) / 100.00D, discordResponse);
    return discordResponse;
  }

  private static void replaceColour(JsonObject base, double revenue, JsonObject discord) {
    String type = base.getString("event_type");

    if (discord.containsKey("components")) {
      JsonArray componentsArray = discord.getJsonArray("components");
      if (componentsArray == null || componentsArray.isEmpty()) {
        return;
      }
      for (Object components : componentsArray) {
        if (!(components instanceof JsonObject component)) {
          continue;
        }

        Integer color = component.getInteger("accent_color");
        if (color != null && color != -1) {
          continue;
        }

        component.put("accent_color", switch (type.toLowerCase(Locale.ROOT)) {
          case "on_order_completed", "on_subscription_activated", "on_subscription_renewed" -> revenue > 0 ? DiscordUtils.convertColour(0, 255, 0) : DiscordUtils.convertColour(128, 128, 128);
          case "on_refund" -> DiscordUtils.convertColour(255, 150, 50);
          default -> DiscordUtils.convertColour(128, 128, 128);
        });
      }
    }

    JsonArray embedsArray = discord.getJsonArray("embeds");
    if (embedsArray == null || embedsArray.isEmpty()) {
      return;
    }

    for (Object embeds : embedsArray) {
      if (!(embeds instanceof JsonObject embed)) {
        continue;
      }

      Integer color = embed.getInteger("color");
      if (color != -1) {
        continue;
      }

      embed.put("color", switch (type.toLowerCase(Locale.ROOT)) {
        case "on_order_completed", "on_subscription_activated", "on_subscription_renewed" -> revenue > 0 ? DiscordUtils.convertColour(0, 255, 0) : DiscordUtils.convertColour(128, 128, 128);
        case "on_refund" -> DiscordUtils.convertColour(255, 150, 50);
        default -> DiscordUtils.convertColour(128, 128, 128);
      });
    }
  }

  public static String getPackagesTable(JsonObject body) {
    JsonArray products = null;
    if (body.containsKey("lines")) {
      products = body.getJsonArray("lines");
    } else if (body.containsKey("product")) {
      products = new JsonArray().add(body.getJsonObject("product"));
    }
    if (products == null || products.isEmpty()) {
      return "";
    }

    String[][] rows = new String[products.size()][3];
    for (int i = 0; i < products.size(); i++) {
      JsonObject product = products.getJsonObject(i);

      // Probably would be better to avoid using optionals,
      // but I don't really care about having code quality in this project.
      rows[i][0] = String.valueOf(product.getInteger("quantity", 1));
      rows[i][1] = Optional.ofNullable(product.getJsonObject("product"))
        .map(json -> json.getString("name", "Missing Name"))
        .orElseGet(() -> product.getString("name", "Missing Name"));
      rows[i][2] = Optional.ofNullable(product.getJsonObject("gift_to_customer"))
        .map(json -> json.getJsonObject("profile"))
        .map(json -> json.getString("name"))
        .orElseGet(() -> {
          JsonObject customer = body.getJsonObject("customer");
          if (customer == null) return "Missing Username";

          JsonObject profile = customer.getJsonObject("profile");
          if (profile == null) return "Missing Username";

          return profile.getString("name", "Missing Username");
        });

      // Attempt to truncate if needed.
      DiscordUtils.truncate(product.containsKey("product") ? product.getJsonObject("product") : product, rows, i);
    }

    // Special characters in the package will break the column width. I ain't fixing that.
    return FlipTable.of(new String[]{"#", "Package", "IGN"}, rows).replace("\n", "\\n");
  }
}

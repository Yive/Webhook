package dev.yive.webhook.utils;

import com.jakewharton.fliptables.FlipTable;
import dev.yive.webhook.MainVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class DiscordUtils {
  private static final List<Pattern> PATTERNS = List.of(
    Pattern.compile("(<tebex:)(.*?)(>)"),
    Pattern.compile("(<spiget:)(.*?)(>)")
  );

  private static final double TEBEX_PLATFORM_FEE = 0.05D; // TODO: Remove this when Tebex adds it to the webhook json.

  private static final int DIVIDER_CHAR_COUNT = 10;
  private static final int DISCORD_EMBED_CHAR_LIMIT = 56; // Might be higher if not using code blocks.

  public static JsonObject parse(JsonObject body, JsonObject discord) {
    String encoded = discord.encode();
    JsonObject tebex = body.getJsonObject("subject");

    // Tebex has a limit of 500 GBP per transaction, so I doubt this needs formatting.
    double revenue = getRevenue(tebex);
    encoded = encoded.replace("<internal:revenue>", String.format("%.2f", revenue));
    encoded = encoded.replace("<internal:type>", tebex.getJsonObject("status").getString("description", "Unknown"));
    encoded = encoded.replace("<internal:version>", MainVerticle.VERSION);
    encoded = encoded.replace("<internal:packages_table>", getPackagesTable(tebex));

    for (Pattern pattern : PATTERNS) {
      encoded = pattern.matcher(encoded).replaceAll(match -> String.valueOf(getNested(match.group(2), body)));
    }

    JsonObject discordResponse = Buffer.buffer(encoded).toJsonObject();
    replaceColour(body, revenue, discordResponse);
    return discordResponse;
  }

  public static double getRevenue(JsonObject tebex) {
    JsonObject json = !(
      Optional.ofNullable(getNested("last_payment.price_paid", tebex))
        .orElse(tebex.getJsonObject("price_paid")) instanceof JsonObject jsonObject
    ) ? null : jsonObject;

    if (json == null || json.isEmpty()) return -1D;

    double paidPrice = json.getDouble("amount", 0.0D);
    double giftCardsPrice = 0;
    if (paidPrice > 0) {
      paidPrice -= (paidPrice * TEBEX_PLATFORM_FEE);

      for (Object object : tebex.getJsonArray("gift_cards")) {
        if (!(object instanceof JsonObject card)) continue;

        giftCardsPrice = giftCardsPrice + card.getJsonObject("amount").getDouble("amount", 0.0D);
      }
    }

    JsonObject fees = tebex.getJsonObject("fees");
    paidPrice -= fees.getJsonObject("tax").getDouble("amount", 0.0D);
    paidPrice -= fees.getJsonObject("gateway").getDouble("amount", 0.0D);
    return Math.max(0, paidPrice - giftCardsPrice);
  }

  public static String getPackagesTable(JsonObject tebex) {
    JsonArray products = !(
      Optional.ofNullable(getNested("last_payment.products", tebex))
        .orElse(tebex.getJsonArray("products")) instanceof JsonArray jsonArray
    ) ? null : jsonArray;

    if (products == null || products.isEmpty()) return "";

    String[][] rows = new String[products.size()][3];
    for (int i = 0; i < products.size(); i++) {
      JsonObject product = products.getJsonObject(i);

      rows[i][0] = String.valueOf(product.getInteger("quantity", 1));
      rows[i][1] = product.getString("name", "Missing Name");
      rows[i][2] = product.getJsonObject("username").getString("username", "Missing Username");

      // Attempt to truncate if needed.
      truncate(product, rows, i);
    }

    return FlipTable.of(new String[]{"#", "Package", "IGN"}, rows).replace("\n", "\\n");
  }

  private static void truncate(JsonObject product, String[][] rows, int index) {
    final int quantityWidth = rows[index][0].length();
    final int productWidth = rows[index][1].length();
    final int usernameWidth = rows[index][2].length();

    if ((DIVIDER_CHAR_COUNT + quantityWidth + productWidth + usernameWidth) > DISCORD_EMBED_CHAR_LIMIT) {
      rows[index][1] = abbreviate(
        product.getString("name", "Unknown Product"),
        DISCORD_EMBED_CHAR_LIMIT - (DIVIDER_CHAR_COUNT + quantityWidth + usernameWidth + 3) // 3 is to allow for 3 dots
      );
    }
  }

  private static String abbreviate(String input, int maxLength) {
    if (input == null || input.length() <= maxLength) {
      return input;
    }

    // Decrease the max length by 3 due to the ellipsis.
    return input.substring(0, maxLength - 3) + "...";
  }

  // Gross way to get the nested
  public static Object getNested(String path, JsonObject json) {
    String[] keys = path.split("\\.");
    Object current = json;
    for (String key : keys) {
      if (!(current instanceof JsonObject jsonObject)) {
        MainVerticle.LOGGER.log(Level.WARNING, "Attempted to get a JSONObject at '" + path + "', but found " + current.getClass().getName());
        return null;
      }
      current = jsonObject.getValue(key);
      if (current == null) return null;
    }

    return current;
  }

  private static void replaceColour(JsonObject tebex, double revenue, JsonObject discord) {
    String type = tebex.getString("type");
    for (Object embeds : discord.getJsonArray("embeds")) {
      if (!(embeds instanceof JsonObject embed)) continue;

      Integer color = embed.getInteger("color");
      if (color != -1) continue;

      embed.put("color", switch (type.toLowerCase(Locale.ROOT)) {
        case "payment.completed", "recurring-payment.started" -> revenue > 0 ? convertColour(0, 255, 0) : convertColour(128, 128, 128);
        case "payment.declined", "recurring-payment.renewed" -> convertColour(255, 150, 50);
        case "payment.dispute.opened", "payment.dispute.lost", "recurring-payment.ended",
             "recurring-payment.cancellation.requested" -> convertColour(255, 0, 0);
        case "payment.dispute.won", "payment.dispute.closed", "recurring-payment.cancellation.aborted" -> convertColour(0, 255, 0);
        default -> convertColour(128, 128, 128);
      });
    }
  }

  private static int convertColour(int r, int g, int b) {
    return ((r & 0x0ff) <<16) | ((g & 0x0ff) <<8) | (b & 0x0ff);
  }
}

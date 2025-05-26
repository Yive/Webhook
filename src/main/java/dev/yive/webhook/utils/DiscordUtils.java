package dev.yive.webhook.utils;

import com.jakewharton.fliptables.FlipTable;
import dev.yive.webhook.MainVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class DiscordUtils {
  private static final List<Pattern> PATTERNS = List.of(
    Pattern.compile("(?<=<tebex:)(.*?)(?=>)"),
    Pattern.compile("(?<=<spiget:)(.*?)(?=>)")
  );

  private static final double TEBEX_PLATFORM_FEE = 0.05D; // TODO: Remove this when Tebex adds it to the webhook json.

  private static final int DIVIDER_CHAR_COUNT = 10;
  private static final int DISCORD_EMBED_CHAR_LIMIT = 56; // Might be higher if not using code blocks.

  public static JsonObject parse(JsonObject body, JsonObject discord) {
    Map<String, Object> map = body.getMap();
    String encoded = discord.encode();

    // Tebex has a limit of 500 GBP per transaction, so I doubt this needs formatting.
    encoded = encoded.replace("<internal:revenue>", String.valueOf(getRevenue(body)));
    encoded = encoded.replace("<internal:type>", body.getString("status.description", "Unknown"));
    encoded = encoded.replace("<internal:version>", MainVerticle.VERSION);
    encoded = encoded.replace("<internal:packages_table>", getPackagesTable(body));

    for (Pattern pattern : PATTERNS) {
      encoded = pattern.matcher(encoded).replaceAll(match -> String.valueOf(map.get(match.group())));
    }

    return Json.decodeValue(encoded, JsonObject.class);
  }

  public static double getRevenue(JsonObject tebex) {
    JsonObject json = Optional.ofNullable(tebex.getJsonObject("paid_price"))
      .orElse(tebex.getJsonObject("last_payment.paid_price"));

    if (json == null || json.isEmpty()) return -1D;

    double paidPrice = json.getDouble("paid_price.amount", 0.0D);
    double giftCardsPrice = 0;
    if (paidPrice > 0) {
      paidPrice = paidPrice - (paidPrice * TEBEX_PLATFORM_FEE);

      for (Object object : tebex.getJsonArray("gift_cards")) {
        if (!(object instanceof JsonObject card)) continue;

        giftCardsPrice = giftCardsPrice + card.getDouble("amount.amount", 0.0D);
      }
    }

    paidPrice = paidPrice - tebex.getDouble("fees.tax.amount", 0.0D);
    paidPrice = paidPrice - tebex.getDouble("fees.gateway.amount", 0.0D);
    return Math.max(0, paidPrice - giftCardsPrice);
  }

  public static String getPackagesTable(JsonObject tebex) {
    JsonArray products = Optional.ofNullable(tebex.getJsonArray("products"))
      .orElse(tebex.getJsonArray("last_payment.products"));

    if (products == null || products.isEmpty()) return "";

    String[][] rows = new String[products.size()][3];
    for (int i = 0; i < products.size(); i++) {
      JsonObject product = products.getJsonObject(i);

      rows[i][0] = String.valueOf(product.getInteger("quantity", 1));
      rows[i][1] = product.getString("name", "Missing Name");
      rows[i][2] = product.getString("username.username", "Missing Username");

      // Attempt to truncate if needed.
      truncate(product, rows, i);
    }

    return FlipTable.of(new String[]{"#", "Package", "IGN"}, rows);
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
}

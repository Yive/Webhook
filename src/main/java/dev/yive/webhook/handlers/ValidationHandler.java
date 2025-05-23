package dev.yive.webhook.handlers;

import dev.yive.webhook.Main;
import dev.yive.webhook.utils.Pair;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.util.JavalinLogger;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValidationHandler implements Handler {
    public static final Set<String> TEBEX_IPS = new HashSet<>();
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        String spigetId = ctx.header("X-Spiget-HookId");
        if (spigetId != null && spigetId.equals(Main.config.getSpiget().getId())) return;
        String ip = ctx.header("cf-connecting-ip");
        if (ip == null) {
            ip = ctx.ip();
        }
        if (ip.equals("127.0.0.1")) return;
        if (!TEBEX_IPS.isEmpty() && !TEBEX_IPS.contains(ip)) {
            ctx.status(HttpStatus.NOT_FOUND);
            JavalinLogger.warn("failed ip check: " + ip);
            return;
        }

        String xSignature = ctx.header("X-Signature");
        if (xSignature == null) {
            ctx.status(HttpStatus.NOT_FOUND);
            JavalinLogger.warn("failed signature check");
            return;
        }

        String[] keys = Main.config.getTebex().getKey().split(",");
        Map<String, Pair<Boolean, String>> results = new HashMap<>(keys.length);
        for (String key : keys) {
            String hex = hmac("HmacSHA256",
                    bytesToHex(
                            MessageDigest.getInstance("SHA-256")
                                    .digest(ctx.body().getBytes(StandardCharsets.UTF_8))
                    ),
                    key
            );
            results.put(key, new Pair<>(xSignature.equals(hex), hex));
        }

        boolean noneMatch = true;
        for (Pair<Boolean, String> pair : results.values()) {
            if (!pair.left()) continue;

            noneMatch = false;
            break;
        }

        if (noneMatch) {
            JavalinLogger.warn("Couldn't verify signature. X-Signature: " + xSignature);
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }

    public static String hmac(String algorithm, String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return bytesToHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte h : hash) {
            String hex = Integer.toHexString(0xff & h);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

package dev.yive.webhook.utils;

import io.javalin.util.JavalinLogger;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebUtil {
    public static void sendRequest(String uri, String data) {
        try (HttpURLConnectionWrapper wrapper = new HttpURLConnectionWrapper(URI.create(uri))) {
            wrapper.getConnection().setRequestMethod("POST");
            wrapper.getConnection().setRequestProperty("User-Agent", "Mozilla/5.0");
            wrapper.getConnection().setRequestProperty("Content-Type", "application/json");
            wrapper.getConnection().setRequestProperty("Accept", "application/json");
            wrapper.getConnection().setDoOutput(true);

            try (OutputStream os = wrapper.getConnection().getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(wrapper.getConnection().getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    builder.append(responseLine.trim());
                }
                if (builder.isEmpty()) return;
                JavalinLogger.info(builder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    private static class HttpURLConnectionWrapper implements AutoCloseable {
        private final HttpURLConnection connection;

        public HttpURLConnectionWrapper(URI uri) throws IOException {
            this(uri.toURL());
        }

        public HttpURLConnectionWrapper(URL url) throws IOException {
            this.connection = (HttpURLConnection) url.openConnection();
        }

        @Override
        public void close() throws Exception {
            this.connection.disconnect();
        }
    }
}

package dev.yive.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.yive.webhook.handlers.ValidationHandler;
import dev.yive.webhook.handlers.disputes.PaymentDisputeClosedHandler;
import dev.yive.webhook.handlers.disputes.PaymentDisputeLostHandler;
import dev.yive.webhook.handlers.disputes.PaymentDisputeOpenedHandler;
import dev.yive.webhook.handlers.disputes.PaymentDisputeWonHandler;
import dev.yive.webhook.handlers.payments.PaymentCompleteHandler;
import dev.yive.webhook.handlers.payments.PaymentDeclinedHandler;
import dev.yive.webhook.handlers.payments.PaymentRefundedHandler;
import dev.yive.webhook.handlers.recurring.RecurringPaymentEndedHandler;
import dev.yive.webhook.handlers.recurring.RecurringPaymentRenewedHandler;
import dev.yive.webhook.handlers.recurring.RecurringPaymentStartedHandler;
import dev.yive.webhook.handlers.spiget.SpigetHandler;
import dev.yive.webhook.json.validation.GenericValidation;
import dev.yive.webhook.json.validation.ValidationResponse;
import dev.yive.webhook.utils.FileUtils;
import dev.yive.webhook.yaml.Config;
import dev.yive.webhook.yaml.Tebex;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.util.JavalinLogger;

import java.io.File;

public class Main {
    public static final String VERSION = "v1.1.17";
    public static Config config;
    public static void main(String[] args) {
        FileUtils.saveResource("config.yml", false);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readValue(new File("config.yml"), Config.class);
        } catch (Exception e) {
            JavalinLogger.error("Failed to load config.yml", e);
            return;
        }

        if (config == null) return;

        Tebex tebex = config.getTebex();
        ValidationHandler.TEBEX_IPS.addAll(tebex.getIps());
        if (ValidationHandler.TEBEX_IPS.isEmpty()) {
            JavalinLogger.error("No IPs found in config.yml");
            return;
        }

        Javalin javalin = Javalin.create().start(config.getIp(), config.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(javalin::stop));

        javalin.before(new ValidationHandler());

        javalin.post("/test-hook", ctx -> {
            ctx.status(HttpStatus.OK);
            GenericValidation validation = ctx.bodyAsClass(GenericValidation.class);
            ctx.json(new ValidationResponse(validation.getId()));
            String body = ctx.body();
            if (body.contains("\"coupons\":[{")) {
                JavalinLogger.info(body);
            }
        });

        javalin.post("/payment-complete", new PaymentCompleteHandler());
        javalin.post("/payment-declined", new PaymentDeclinedHandler());
        javalin.post("/payment-refunded", new PaymentRefundedHandler());

        javalin.post("/payment-dispute-opened", new PaymentDisputeOpenedHandler());
        javalin.post("/payment-dispute-won", new PaymentDisputeWonHandler());
        javalin.post("/payment-dispute-lost", new PaymentDisputeLostHandler());
        javalin.post("/payment-dispute-closed", new PaymentDisputeClosedHandler());

        javalin.post("/recurring-payment-started", new RecurringPaymentStartedHandler());
        javalin.post("/recurring-payment-renewed", new RecurringPaymentRenewedHandler());
        javalin.post("/recurring-payment-ended", new RecurringPaymentEndedHandler());

        javalin.post("/spiget", new SpigetHandler());
    }
}
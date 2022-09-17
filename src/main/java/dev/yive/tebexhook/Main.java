package dev.yive.tebexhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dev.yive.tebexhook.handlers.ValidationHandler;
import dev.yive.tebexhook.handlers.disputes.PaymentDisputeClosedHandler;
import dev.yive.tebexhook.handlers.disputes.PaymentDisputeLostHandler;
import dev.yive.tebexhook.handlers.disputes.PaymentDisputeOpenedHandler;
import dev.yive.tebexhook.handlers.disputes.PaymentDisputeWonHandler;
import dev.yive.tebexhook.handlers.payments.PaymentCompleteHandler;
import dev.yive.tebexhook.handlers.payments.PaymentDeclinedHandler;
import dev.yive.tebexhook.handlers.payments.PaymentRefundedHandler;
import dev.yive.tebexhook.handlers.recurring.RecurringPaymentEndedHandler;
import dev.yive.tebexhook.handlers.recurring.RecurringPaymentRenewedHandler;
import dev.yive.tebexhook.handlers.recurring.RecurringPaymentStartedHandler;
import dev.yive.tebexhook.json.validation.GenericValidation;
import dev.yive.tebexhook.json.validation.ValidationResponse;
import dev.yive.tebexhook.utils.FileUtils;
import dev.yive.tebexhook.yaml.Config;
import dev.yive.tebexhook.yaml.Tebex;
import io.javalin.Javalin;
import io.javalin.core.util.JavalinLogger;
import io.javalin.http.HttpCode;

import java.io.File;

public class Main {
    public static Config config;
    public static void main(String[] args) {
        FileUtils.saveResource("config.yml", false);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            config = mapper.readValue(new File("config.yml"), Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (config == null) return;

        Tebex tebex = config.getTebex();
        ValidationHandler.TEBEX_IPS.addAll(tebex.getIps());

        Javalin javalin = Javalin.create().start(7070);
        Runtime.getRuntime().addShutdownHook(new Thread(javalin::stop));

        javalin.before(new ValidationHandler());

        javalin.post("/test-hook", ctx -> {
            ctx.status(HttpCode.OK);
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
    }
}
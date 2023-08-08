package dev.yive.webhook.handlers.recurring;

import dev.yive.webhook.Main;
import dev.yive.webhook.json.validation.ValidationRecurringPayment;
import dev.yive.webhook.json.validation.ValidationResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

// TODO: I don't have access to the json that Tebex sends for this.
public class RecurringPaymentRenewedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getRecurring().getRenewed().isEnabled()) return;
        ValidationRecurringPayment payment = ctx.bodyAsClass(ValidationRecurringPayment.class);
        ctx.json(new ValidationResponse(payment.getId()));
    }
}

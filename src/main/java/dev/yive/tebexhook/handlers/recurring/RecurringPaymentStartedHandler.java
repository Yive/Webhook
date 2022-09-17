package dev.yive.tebexhook.handlers.recurring;

import dev.yive.tebexhook.Main;
import dev.yive.tebexhook.json.validation.ValidationRecurringPayment;
import dev.yive.tebexhook.json.validation.ValidationResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class RecurringPaymentStartedHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        if (!Main.config.getRecurring().getStarted().isEnabled()) return;
        ValidationRecurringPayment payment = ctx.bodyAsClass(ValidationRecurringPayment.class);
        ctx.json(new ValidationResponse(payment.getId()));
    }
}

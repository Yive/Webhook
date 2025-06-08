package dev.yive.webhook.services.tebex.payments;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class PaymentDeclinedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/payments/declined.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/payments/declined.json";
  }
}

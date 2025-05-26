package dev.yive.webhook.services.tebex.payments;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class PaymentCompleteHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/payments/complete.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/payments/complete.json";
  }
}

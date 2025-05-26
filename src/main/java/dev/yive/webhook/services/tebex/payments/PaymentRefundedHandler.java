package dev.yive.webhook.services.tebex.payments;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class PaymentRefundedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/payments/refunded.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/payments/refunded.json";
  }
}

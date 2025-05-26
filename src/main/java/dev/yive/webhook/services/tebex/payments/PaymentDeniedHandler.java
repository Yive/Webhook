package dev.yive.webhook.services.tebex.payments;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class PaymentDeniedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/payments/denied.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/payments/denied.json";
  }
}

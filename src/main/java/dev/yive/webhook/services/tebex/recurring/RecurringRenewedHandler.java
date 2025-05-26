package dev.yive.webhook.services.tebex.recurring;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class RecurringRenewedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/recurring/renewed.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/recurring/renewed.json";
  }
}

package dev.yive.webhook.services.paynow.subscription;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class RenewedHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/subscription/renewed.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/subscription/renewed.json";
  }
}

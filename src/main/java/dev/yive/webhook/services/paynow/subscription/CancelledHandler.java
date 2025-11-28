package dev.yive.webhook.services.paynow.subscription;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class CancelledHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/subscription/cancelled.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/subscription/cancelled.json";
  }
}

package dev.yive.webhook.services.paynow.order;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class CompletedHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/order/completed.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/order/completed.json";
  }
}

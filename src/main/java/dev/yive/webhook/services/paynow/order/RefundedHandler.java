package dev.yive.webhook.services.paynow.order;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class RefundedHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/order/refund.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/order/refund.json";
  }
}

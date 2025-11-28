package dev.yive.webhook.services.paynow.order;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class ChargebackHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/order/chargeback.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/order/chargeback.json";
  }
}

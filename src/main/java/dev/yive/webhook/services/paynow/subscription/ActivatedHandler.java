package dev.yive.webhook.services.paynow.subscription;

import dev.yive.webhook.services.paynow.GenericPayNowHandler;

public class ActivatedHandler implements GenericPayNowHandler {
  @Override
  public String config() {
    return "services/paynow/subscription/activated.yml";
  }

  @Override
  public String discord() {
    return "services/paynow/subscription/activated.json";
  }
}

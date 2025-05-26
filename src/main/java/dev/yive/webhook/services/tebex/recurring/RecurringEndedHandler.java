package dev.yive.webhook.services.tebex.recurring;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class RecurringEndedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/recurring/ended.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/recurring/ended.json";
  }
}

package dev.yive.webhook.services.tebex.recurring;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class RecurringStartedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/recurring/started.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/recurring/started.json";
  }
}

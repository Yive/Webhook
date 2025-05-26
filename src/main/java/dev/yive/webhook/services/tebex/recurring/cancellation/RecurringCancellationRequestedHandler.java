package dev.yive.webhook.services.tebex.recurring.cancellation;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

public class RecurringCancellationRequestedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/recurring/cancellation/requested.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/recurring/cancellation/requested.json";
  }
}

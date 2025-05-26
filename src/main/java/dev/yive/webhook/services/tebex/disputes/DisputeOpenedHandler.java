package dev.yive.webhook.services.tebex.disputes;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

// Can't use any other HTTP codes other than success.
public class DisputeOpenedHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/disputes/opened.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/disputes/opened.json";
  }
}

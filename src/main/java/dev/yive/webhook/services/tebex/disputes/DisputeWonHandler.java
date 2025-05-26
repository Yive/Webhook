package dev.yive.webhook.services.tebex.disputes;

import dev.yive.webhook.services.tebex.GenericTebexHandler;

// Can't use any other HTTP codes other than success.
public class DisputeWonHandler implements GenericTebexHandler {
  @Override
  public String config() {
    return "services/tebex/disputes/won.yml";
  }

  @Override
  public String discord() {
    return "services/tebex/disputes/won.json";
  }
}

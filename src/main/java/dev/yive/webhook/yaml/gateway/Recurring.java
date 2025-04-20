package dev.yive.webhook.yaml.gateway;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Recurring {
    private Gateway ended;
    private Gateway renewed;
    private Gateway started;
    @JsonAlias("cancellation-requested")
    private Gateway cancellationRequested;
    @JsonAlias("cancellation-aborted")
    private Gateway cancellationAborted;
}

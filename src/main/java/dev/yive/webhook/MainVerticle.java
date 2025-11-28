package dev.yive.webhook;

import dev.yive.webhook.services.paynow.PayNowValidationHandler;
import dev.yive.webhook.services.paynow.order.ChargebackHandler;
import dev.yive.webhook.services.paynow.order.CompletedHandler;
import dev.yive.webhook.services.paynow.order.RefundedHandler;
import dev.yive.webhook.services.paynow.subscription.ActivatedHandler;
import dev.yive.webhook.services.paynow.subscription.CancelledHandler;
import dev.yive.webhook.services.paynow.subscription.RenewedHandler;
import dev.yive.webhook.services.tebex.TebexValidationHandler;
import dev.yive.webhook.services.tebex.disputes.DisputeClosedHandler;
import dev.yive.webhook.services.tebex.disputes.DisputeLostHandler;
import dev.yive.webhook.services.tebex.disputes.DisputeOpenedHandler;
import dev.yive.webhook.services.tebex.disputes.DisputeWonHandler;
import dev.yive.webhook.services.tebex.payments.PaymentCompleteHandler;
import dev.yive.webhook.services.tebex.payments.PaymentDeclinedHandler;
import dev.yive.webhook.services.tebex.payments.PaymentRefundedHandler;
import dev.yive.webhook.services.tebex.recurring.RecurringEndedHandler;
import dev.yive.webhook.services.tebex.recurring.RecurringRenewedHandler;
import dev.yive.webhook.services.tebex.recurring.RecurringStartedHandler;
import dev.yive.webhook.services.tebex.recurring.cancellation.RecurringCancellationAbortedHandler;
import dev.yive.webhook.services.tebex.recurring.cancellation.RecurringCancellationRequestedHandler;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends VerticleBase {
  public static final String VERSION = MainVerticle.class.getPackage().getImplementationVersion();
  public static final Logger LOGGER = Logger.getLogger("Webhook");
  public static final Map<String, ConfigRetriever> CONFIGS = new ConcurrentHashMap<>();

  private static final String[] PATHS = new String[]{
    // Actual configs
    "config.yml",
    // TODO: Maybe support the other webhook events... idk what they do though.
    "services/paynow/order/chargeback.yml",
    "services/paynow/order/completed.yml",
    "services/paynow/order/refunded.yml",
    "services/paynow/subscription/activated.yml",
    "services/paynow/subscription/cancelled.yml",
    "services/paynow/subscription/renewed.yml",
    "services/paynow/paynow.yml",
    "services/other/spiget.yml",
    "services/tebex/disputes/closed.yml",
    "services/tebex/disputes/lost.yml",
    "services/tebex/disputes/opened.yml",
    "services/tebex/disputes/won.yml",
    "services/tebex/payments/complete.yml",
    "services/tebex/payments/declined.yml",
    "services/tebex/payments/refunded.yml",
    "services/tebex/recurring/cancellation/aborted.yml",
    "services/tebex/recurring/cancellation/requested.yml",
    "services/tebex/recurring/ended.yml",
    "services/tebex/recurring/renewed.yml",
    "services/tebex/recurring/started.yml",
    "services/tebex/tebex.yml",
    // Discord Embed JSONs
    "services/paynow/order/chargeback.json",
    "services/paynow/order/completed.json",
    "services/paynow/order/refunded.json",
    "services/paynow/subscription/activated.json",
    "services/paynow/subscription/cancelled.json",
    "services/paynow/subscription/renewed.json",
    "services/other/spiget.json",
    "services/tebex/disputes/closed.json",
    "services/tebex/disputes/lost.json",
    "services/tebex/disputes/opened.json",
    "services/tebex/disputes/won.json",
    "services/tebex/payments/complete.json",
    "services/tebex/payments/declined.json",
    "services/tebex/payments/refunded.json",
    "services/tebex/recurring/cancellation/aborted.json",
    "services/tebex/recurring/cancellation/requested.json",
    "services/tebex/recurring/ended.json",
    "services/tebex/recurring/renewed.json",
    "services/tebex/recurring/started.json"
  };

  private static MainVerticle instance;
  public static MainVerticle getInstance() {
    return instance;
  }

  private WebClient webClient;
  public WebClient getWebClient() {
    return webClient;
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    instance = this;
    webClient = WebClient.create(vertx);
    for (String path : PATHS) {
      if (path.contains("spiget")) continue; // TODO: Support Spiget
      try {
        String[] split = path.split("/");

        Path configs = Path.of("configs", split);
        File file = configs.toFile();

        ConfigStoreOptions options = new ConfigStoreOptions();
        options.setType("file");
        options.setFormat(path.endsWith(".yml") ? "yaml" : "json");
        options.setConfig(new JsonObject().put("path", configs));

        CONFIGS.put(path, ConfigRetriever.create(vertx, new ConfigRetrieverOptions().addStore(options)));

        if (file.exists()) continue;

        file.getParentFile().mkdirs();
        Files.copy(MainVerticle.class.getClassLoader().getResourceAsStream(path), configs);
      } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Failed to load config at '" + path + "'", e);
      }
    }
  }

  @Override
  public Future<?> start() {
    // I'm not a big fan of this, but at least it'll force each config to be cached.
    return Future.all(CONFIGS.values().stream().map(ConfigRetriever::getConfig).toList()).compose(unused -> {
      // Create a Router
      Router router = Router.router(vertx);

      //router.route(HttpMethod.POST, "/spiget").handler(new SpigetHandler()); // TODO: Support Spiget

      // All PayNow paths need the validation handler.
      router.route(HttpMethod.POST, "/paynow/order/chargeback")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new ChargebackHandler());
      router.route(HttpMethod.POST, "/paynow/order/completed")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new CompletedHandler());
      router.route(HttpMethod.POST, "/paynow/order/refund")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new RefundedHandler());
      router.route(HttpMethod.POST, "/paynow/subscription/activated")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new ActivatedHandler());
      router.route(HttpMethod.POST, "/paynow/subscription/cancelled")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new CancelledHandler());
      router.route(HttpMethod.POST, "/paynow/subscription/renewed")
        .handler(BodyHandler.create())
        .handler(new PayNowValidationHandler())
        .handler(new RenewedHandler());

      // All Tebex paths need the validation handler.
      // TODO: Change these to be in a similar format to paynow.
      router.route(HttpMethod.POST, "/payment-dispute-closed")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new DisputeClosedHandler());
      router.route(HttpMethod.POST, "/payment-dispute-lost")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new DisputeLostHandler());
      router.route(HttpMethod.POST, "/payment-dispute-opened")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new DisputeOpenedHandler());
      router.route(HttpMethod.POST, "/payment-dispute-won")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new DisputeWonHandler());

      router.route(HttpMethod.POST, "/payment-complete")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new PaymentCompleteHandler());
      router.route(HttpMethod.POST, "/payment-declined")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new PaymentDeclinedHandler());
      router.route(HttpMethod.POST, "/payment-refunded")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new PaymentRefundedHandler());

      router.route(HttpMethod.POST, "/recurring-payment-cancellation-aborted")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new RecurringCancellationAbortedHandler());
      router.route(HttpMethod.POST, "/recurring-payment-cancellation-requested")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new RecurringCancellationRequestedHandler());
      router.route(HttpMethod.POST, "/recurring-payment-ended")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new RecurringEndedHandler());
      router.route(HttpMethod.POST, "/recurring-payment-renewed")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new RecurringRenewedHandler());
      router.route(HttpMethod.POST, "/recurring-payment-started")
        .handler(BodyHandler.create())
        .handler(new TebexValidationHandler())
        .handler(new RecurringStartedHandler());

      for (Route route : router.getRoutes()) {
        route.failureHandler(event ->  {
          LOGGER.log(Level.WARNING, "Error occurred on path: " + event.request().path(), event.failure());
          event.response().end();
        });
      }

      return vertx.createHttpServer()
        .requestHandler(router)
        .listen(
          Optional.ofNullable(CONFIGS.get("config.yml"))
            .map(config -> config.getCachedConfig().getInteger("port"))
            .orElse(8080)
        )
        .onSuccess(server -> LOGGER.log(Level.INFO, "Webhook v" + VERSION + " started on port: " + server.actualPort()))
        .onFailure(throwable -> LOGGER.log(Level.WARNING, "Failed to start webhook", throwable));
    });
  }
}

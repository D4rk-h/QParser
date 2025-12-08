package infrastructure.config;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavalinServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(JavalinServerConfig.class);

    public Javalin create(int port) {
        return Javalin.create(this::configure)
                .events(event -> {
                    event.serverStarted(() -> logger.info("Javalin server started on port {}", port));
                    event.serverStopped(() -> logger.info("Javalin server stopped"));
                });
    }

    private void configure(JavalinConfig config) {
        config.bundledPlugins.enableCors(this::configureCors);
        config.requestLogger.http((ctx, ms) ->
                logger.info("{} {} - {:.2f}ms", ctx.method(), ctx.path(), ms)
        );
        config.jsonMapper(JsonMapperFactory.create());
        config.http.defaultContentType = "application/json";
        config.bundledPlugins.enableDevLogging();
    }

    private void configureCors(CorsPluginConfig cors) {
        cors.addRule(rule -> {
            rule.allowHost("http://localhost:3000", "http://localhost:8080");
            rule.allowCredentials = false;
        });
    }
}

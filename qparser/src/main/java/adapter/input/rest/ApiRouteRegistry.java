package adapter.input.rest;

import adapter.input.CircuitController;
import adapter.input.rest.dto.ErrorResponseDto;
import infrastructure.web.RouteRegistry;
import io.javalin.Javalin;

public class ApiRouteRegistry implements RouteRegistry {

    private final CircuitController circuitController;
    private final HealthController healthController;

    public ApiRouteRegistry(CircuitController circuitController, HealthController healthController) {
        this.circuitController = circuitController;
        this.healthController = healthController;
    }

    @Override
    public void registerRoutes(Javalin app) {
        app.get("/health", healthController::health);
        app.get("/api/version", healthController::version);
        app.post("/api/parse", circuitController::parseCircuit);
        app.get("/api/supported-types", circuitController::getSupportedTypes);
        app.error(404, ctx ->
                ctx.status(404).json(new ErrorResponseDto("Not Found",
                        "Endpoint does not exist"))
        );
    }
}

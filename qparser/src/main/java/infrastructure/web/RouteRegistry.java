package infrastructure.web;

import io.javalin.Javalin;

public interface RouteRegistry {
    void registerRoutes(Javalin app);
}

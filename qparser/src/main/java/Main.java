import infrastructure.config.DependencyContainer;
import infrastructure.config.JavalinServerConfig;
import infrastructure.web.RouteRegistry;
import io.javalin.Javalin;

public class Main {
    private static final String DEFAULT_PORT = "8080";

    public static void main(String[] args) {
        int port = getPort(args);
        DependencyContainer container = new DependencyContainer();
        JavalinServerConfig serverConfig = new JavalinServerConfig();
        Javalin app = serverConfig.create(port);
        RouteRegistry routeRegistry = container.routeRegistry();
        routeRegistry.registerRoutes(app);
        app.start(port);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static int getPort(String[] args) {
        if (args.length > 0) {
            try {return Integer.parseInt(args[0]);}
            catch (NumberFormatException e) {
                System.err.println("Invalid port argument, using default: " + DEFAULT_PORT);
            }
        }
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {return Integer.parseInt(envPort);
            } catch (NumberFormatException e) {
                System.err.println("Invalid PORT environment variable, using default: " + DEFAULT_PORT);
            }
        }
        return Integer.parseInt(DEFAULT_PORT);
    }
}

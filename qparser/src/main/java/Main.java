import infrastructure.config.DependencyContainer;
import infrastructure.config.JavalinServerConfig;
import infrastructure.web.RouteRegistry;
import io.javalin.Javalin;

public class Main {
    private static final String DEFAULT_PORT = System.getenv("DEFAULT_PORT");

    public static void main(String[] args) throws NoSuchMethodException {
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
            catch (NumberFormatException e) {System.err.println("Invalid port, using default: " + DEFAULT_PORT);}
        }
        return Integer.parseInt(DEFAULT_PORT);
    }
}

package infrastructure.config;

import adapter.input.CircuitController;
import adapter.input.rest.ApiRouteRegistry;
import adapter.input.rest.HealthController;
import adapter.input.rest.HttpExceptionHandler;
import adapter.input.rest.mapper.CircuitDtoMapper;
import adapter.output.parser.QasmParserAdapter;
import adapter.output.parser.QiskitParserAdapter;
import application.usecase.ParseCircuitUseCaseImpl;
import domain.port.input.ParseCircuitUseCase;
import domain.port.output.CircuitParser;
import domain.service.CircuitParsingService;
import infrastructure.web.RouteRegistry;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {

    public static CircuitParser qasmParser() {
        return new QasmParserAdapter();
    }

    public CircuitParser qiskitParser() {
        return new QiskitParserAdapter();
    }

    public Map<String, CircuitParser> parsers() {
        Map<String, CircuitParser> parsers = new HashMap<>();
        parsers.put(qasmParser().getSupportedType(), qasmParser());
        parsers.put(qiskitParser().getSupportedType(), qiskitParser());
        return parsers;
    }

    public CircuitParsingService circuitParsingService() {
        return new CircuitParsingService(parsers());
    }

    public ParseCircuitUseCase parseCircuitUseCase() {
        return new ParseCircuitUseCaseImpl(circuitParsingService());
    }

    public CircuitDtoMapper circuitDtoMapper() {
        return new CircuitDtoMapper();
    }

    public HttpExceptionHandler httpExceptionHandler() {
        return new HttpExceptionHandler();
    }

    public CircuitController circuitController() throws NoSuchMethodException {
        return new CircuitController(
                parseCircuitUseCase(),
                circuitDtoMapper(),
                httpExceptionHandler()
        );
    }

    public HealthController healthController() {
        return new HealthController("1.0.0", "Qparser API");
    }

    public RouteRegistry routeRegistry() throws NoSuchMethodException {
        return new ApiRouteRegistry(
                circuitController(),
                healthController()
        );
    }
}


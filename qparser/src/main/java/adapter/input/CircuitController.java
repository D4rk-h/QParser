package adapter.input;

import adapter.input.rest.HttpExceptionHandler;
import adapter.input.rest.dto.CircuitResponseDto;
import adapter.input.rest.dto.ParseRequestDto;
import adapter.input.rest.dto.ParsedScriptResponseDto;
import adapter.input.rest.mapper.CircuitDtoMapper;
import domain.model.Circuit;
import domain.port.input.ParseCircuitUseCase;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class CircuitController {
    private final ParseCircuitUseCase parser;
    private final CircuitDtoMapper mapper;
    private final HttpExceptionHandler exceptionHandler;

    public CircuitController(ParseCircuitUseCase parser, CircuitDtoMapper mapper, HttpExceptionHandler httpExceptionHandler) {
        this.parser = parser;
        this.mapper = mapper;
        this.exceptionHandler = httpExceptionHandler;
    }

    public void parseScriptToCircuit(Context ctx) {
        try {
            ParseRequestDto request = ctx.bodyAsClass(ParseRequestDto.class);
            Circuit circuit = parser.executeScriptToCircuit(
                    request.script(),
                    request.scriptType()
            );
            if (request.desiredType() != null && !request.desiredType().trim().isEmpty()) {
                String parsedScript = parser.executeCircuitToScript(circuit, request.desiredType());
                ParsedScriptResponseDto response = new ParsedScriptResponseDto(parsedScript, request.desiredType());
                ctx.json(response).status(HttpStatus.OK);
            } else {
                CircuitResponseDto response = mapper.toDto(circuit);
                ctx.json(response).status(HttpStatus.OK);
            }
        } catch (Exception e) {
            exceptionHandler.handle(e, ctx);
        }
    }

    public void getSupportedTypes(Context ctx) {
        ctx.json(new String[]{"QASM", "QISKIT"});
    }
}

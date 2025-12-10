package adapter.input.rest;

import domain.exception.ParsingException;
import domain.exception.UnsupportedScriptTypeException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class HttpExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpExceptionHandler.class);

    public void handle(Exception e, Context ctx) {
        if (e instanceof ParsingException) {
            handleParsingException((ParsingException) e, ctx);
        } else if (e instanceof UnsupportedScriptTypeException) {
            handleUnsupportedType((UnsupportedScriptTypeException) e, ctx);
        } else if (e instanceof IllegalArgumentException) {
            handleValidationError((IllegalArgumentException) e, ctx);
        } else {
            handleGenericError(e, ctx);
        }
    }

    private void handleParsingException(ParsingException e, Context ctx) {
        logger.error("Parsing error: {}", e.getMessage(), e);
        ctx.status(400).json(createErrorResponse("ParsingError", e.getMessage()));
    }

    private void handleUnsupportedType(UnsupportedScriptTypeException e, Context ctx) {
        logger.warn("Unsupported script type: {}", e.getMessage());
        ctx.status(400).json(Map.of(
                "error", "UnsupportedScriptType",
                "message", e.getMessage(),
                "supportedTypes", new String[]{"QASM", "QISKIT"}
        ));
    }

    private void handleValidationError(IllegalArgumentException e, Context ctx) {
        ctx.status(400).json(createErrorResponse("ValidationError", e.getMessage()));
    }

    private void handleGenericError(Exception e, Context ctx) {
        logger.error("Unexpected error", e);
        ctx.status(500).json(createErrorResponse("InternalServerError",
                "An unexpected error occurred"));
    }

    private Map<String, Object> createErrorResponse(String error, String message) {
        return Map.of(
                "error", error,
                "message", message,
                "timestamp", System.currentTimeMillis()
        );
    }
}
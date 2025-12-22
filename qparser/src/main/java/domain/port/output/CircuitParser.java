package domain.port.output;

import domain.exception.ParsingException;
import domain.model.Circuit;

public interface CircuitParser {
    Circuit parseScript(String script) throws ParsingException;
    String parseObject(Circuit circuit) throws ParsingException;
    String getSupportedType();
}

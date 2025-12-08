package domain.port.output;

import domain.exception.ParsingException;
import domain.model.Circuit;

public interface CircuitParser {
    Circuit parse(String script) throws ParsingException;
}

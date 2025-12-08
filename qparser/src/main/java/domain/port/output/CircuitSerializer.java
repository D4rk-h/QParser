package domain.port.output;

import domain.exception.ParsingException;

public interface CircuitSerializer {
    String serialize(Object circuit) throws ParsingException;
}

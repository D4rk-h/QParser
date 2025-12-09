package domain.port.input;

import domain.exception.ParsingException;
import domain.model.Circuit;

public interface ParseCircuitUseCase {
    Circuit execute(String script, String scriptType) throws ParsingException;
}

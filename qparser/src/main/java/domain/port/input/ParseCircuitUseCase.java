package domain.port.input;

import domain.exception.ParsingException;
import domain.model.Circuit;

public interface ParseCircuitUseCase {
    Circuit executeScriptToCircuit(String script, String scriptType) throws ParsingException;
    String executeCircuitToScript(Circuit circuit, String scriptType) throws ParsingException;
}

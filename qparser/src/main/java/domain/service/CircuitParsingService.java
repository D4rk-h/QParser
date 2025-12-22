package domain.service;

import domain.exception.ParsingException;
import domain.exception.UnsupportedScriptTypeException;
import domain.model.Circuit;
import domain.port.output.CircuitParser;
import java.util.Map;

public class CircuitParsingService {
    private final Map<String, CircuitParser> parsers;

    public CircuitParsingService(Map<String, CircuitParser> parsers) {
        this.parsers = parsers;
    }

    public Circuit parseScriptToCircuit(String script, String scriptType) throws ParsingException {
        if (script == null || script.trim().isEmpty()) throw new ParsingException("Script cannot be empty");
        if (scriptType == null || scriptType.trim().isEmpty()) throw new ParsingException("Script type must be specified");

        CircuitParser parser = parsers.get(scriptType.toUpperCase());
        if (parser == null) throw new UnsupportedScriptTypeException("No parser available for script type: " + scriptType);

        Circuit circuit = parser.parseScript(script);
        validateCircuit(circuit);
        return circuit;
    }

    public String parseCircuitToScript(Circuit circuit, String scriptType) throws ParsingException {
        if (circuit == null) throw new ParsingException("Circuit cannot be null");
        if (scriptType == null || scriptType.trim().isEmpty()) throw new ParsingException("Script type must be specified");

        CircuitParser parser = parsers.get(scriptType.toUpperCase());
        if (parser == null) throw new UnsupportedScriptTypeException("No parser available for script type: " + scriptType);

        return parser.parseObject(circuit);
    }

    private void validateCircuit(Circuit circuit) throws ParsingException {
        if (circuit.getAllGates() <= 0 && circuit.getAllMeasurements()<0) throw new ParsingException("Parsed circuit is empty");
    }
}

package adapter.output.parser.qasm;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.port.output.CircuitParser;

import java.util.ArrayList;
import java.util.List;

public class QasmParserAdapter implements CircuitParser {
    private final String supportedType = "QASM";
    private final List<String> rotationGates = List.of("rx", "rz", "ry", "u", "rxx", "rzz");
    private final List<String> controlledGates = List.of(
            "cx", "cz", "ch", "ccx", "swap", "c3x", "c4x", "cp", "cswap", "crx", "cry", "crz", "cu");
    private final List<String> oneQubitGates = List.of("h", "x", "y", "z", "s", "sdg", "t", "tdg", "id");
    private final QasmParsingUtils utils = new QasmParsingUtils();

    public QasmParserAdapter() {}

    @Override
    public Circuit parseScript(String script) throws ParsingException {
        String[] lines = script.split("\n");
        if (lines.length == 0 || !lines[0].contains("OPENQASM")) throw new ParsingException("Wrong script format: Expected qasm");
        int nQubits = 0;
        int nBits = 0;
        List<CircuitLayer> layers = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("//")) continue;
            if (line.contains("qreg")) nQubits = utils.extractNumber(line);
            else if (line.contains("creg")) nBits = utils.extractNumber(line);
            else if (line.contains("barrier") || line.contains("OPENQASM") || line.contains("include")) continue;
            else {
                CircuitLayer layer = utils.processLine(line, rotationGates, controlledGates, oneQubitGates);
                if (layer != null) layers.add(layer);
            }
        }
        return new Circuit(nQubits, nBits, layers);
    }

    @Override
    public String parseObject(Circuit circuit) throws ParsingException {
        return utils.parseObject(circuit, circuit.numberOfQubits(), circuit.numberOfClBits());
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}
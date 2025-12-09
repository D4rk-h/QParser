package adapter.output.parser;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.port.output.CircuitParser;
import java.util.Arrays;
import java.util.List;

public class QasmParserAdapter implements CircuitParser { // todo: finish lists of gates and add special cases as rccx rc3x sx
    private final String supportedType = "QASM";
    private final List<String> rotationGates = List.of("rx");
    private final List<String> controlledGates = List.of("cx");
    private final List<String> oneQubitGates = List.of("h");

    public QasmParserAdapter() {
    }

    @Override
    public Circuit parse(String script) throws ParsingException {
        List<String> splitScript = List.of(script.split("\n"));
        if (!splitScript.getFirst().contains("OPENQASM")) throw new ParsingException("Wrong script format: Expected qasm");
        splitScript.addAll(Arrays.asList(script.split("\n")));
        for (String line: splitScript) {
            if (line.contains("qreg")) {
                int nQubits = line.charAt(7);
            } else if(line.contains("creg")) {
                int nBits = line.charAt(7);
            } else if(line.contains("measure")) {
                int qubitIndex = line.charAt(10);
                int bitIndex = line.charAt(18);
            };

        }
        return null;
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

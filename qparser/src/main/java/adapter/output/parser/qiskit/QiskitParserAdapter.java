package adapter.output.parser.qiskit;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.port.output.CircuitParser;
import java.util.ArrayList;
import java.util.List;

public class QiskitParserAdapter implements CircuitParser {
    private final String supportedType = "QISKIT";
    private final QiskitParsingUtils utils = new QiskitParsingUtils();

    public QiskitParserAdapter() {}

    @Override
    public Circuit parseScript(String script) throws ParsingException {
        int nQubits = 0;
        int nClBits = 0;
        String[] lines = script.split("\n");
        List<CircuitLayer> layers = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.contains("QuantumRegister(")) nQubits = Integer.parseInt(String.valueOf(line.split(" ")[2].charAt(16)));
            if (line.contains("ClassicalRegister(")) nClBits = Integer.parseInt(String.valueOf(line.split(" ")[2].charAt(18)));
            CircuitLayer layer = utils.processLine(line);
            if (layer != null) layers.add(layer);
        }
        return new Circuit(nQubits, nClBits, layers);
    }

    @Override
    public String parseObject(Circuit circuit) throws ParsingException {
        int nQubits = circuit.numberOfQubits();
        int nClBits = circuit.numberOfClBits();
        return utils.parseObject(circuit, nQubits, nClBits);
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

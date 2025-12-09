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
    public Circuit parse(String script) throws ParsingException {
        String[] lines = script.split("\n");

        return null;
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

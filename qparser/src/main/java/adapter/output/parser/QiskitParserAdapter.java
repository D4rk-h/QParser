package adapter.output.parser;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.port.output.CircuitParser;
import java.util.stream.Stream;

public class QiskitParserAdapter implements CircuitParser {
    private final String supportedType = "QISKIT";

    public QiskitParserAdapter() {}

    @Override
    public Circuit parse(String script) throws ParsingException {
        Stream<String> scriptLines = script.lines();
        return null;
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

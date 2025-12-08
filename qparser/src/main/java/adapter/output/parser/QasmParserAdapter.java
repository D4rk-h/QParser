package adapter.output.parser;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.port.output.CircuitParser;

public class QasmParserAdapter implements CircuitParser {
    private final String supportedType = "QASM";

    public QasmParserAdapter() {
    }

    @Override
    public Circuit parse(String script) throws ParsingException {
        return null; // todo: develop parse method in both parsers
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

package application.usecase;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.port.input.ParseCircuitUseCase;
import domain.service.CircuitParsingService;

public class ParseCircuitUseCaseImpl implements ParseCircuitUseCase {
    private final CircuitParsingService service;

    public ParseCircuitUseCaseImpl(CircuitParsingService service) {
        this.service = service;
    }

    @Override
    public Circuit execute(String script, String scriptType) throws ParsingException {
        return service.parseCircuit(script, scriptType);
    }
}

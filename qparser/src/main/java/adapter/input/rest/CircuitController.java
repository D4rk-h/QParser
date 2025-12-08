package adapter.input.rest;

import adapter.input.rest.mapper.CircuitDtoMapper;
import domain.port.input.ParseCircuitUseCase;


public class CircuitController {
    private final ParseCircuitUseCase parser;
    private final CircuitDtoMapper mapper;

    public CircuitController(ParseCircuitUseCase parser, CircuitDtoMapper mapper) {
        this.parser = parser;
        this.mapper = mapper;
    }


}

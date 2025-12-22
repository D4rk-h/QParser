package adapter.input.rest.dto;

import java.util.List;

public record CircuitLayerDto (
        int numberOfGates,
        int numberOfMeasurements,
        List<GateDto> gates,
        List<MeasurementDto> measurements
    )
{ }

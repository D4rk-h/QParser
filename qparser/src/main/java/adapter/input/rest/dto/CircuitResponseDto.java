package adapter.input.rest.dto;

import java.util.List;

public record CircuitResponseDto(
        int numberOfQubits,
        int numberOfClBits,
        int totalGates,
        int totalMeasurements,
        List<CircuitLayerDto> layers
) {}


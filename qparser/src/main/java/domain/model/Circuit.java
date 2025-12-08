package domain.model;

import java.util.List;

public record Circuit(
        String numberOfQubits,
        String numberOfMeasurements,
        List<CircuitLayer> layers
)
{}

package domain.model;

import java.util.List;

public record Circuit(
        String numberOfQubits,
        List<CircuitLayer> layers
)
{
    public int getAllGates() {
        int count = 0;
        for (CircuitLayer layer: this.layers()) count += layer.getNumberOfGates();
        return count;
    }

    public int getAllMeasurements() {
        int count = 0;
        for (CircuitLayer layer: this.layers()) count += layer.getNumberOfMeasurements();
        return count;
    }
}

package domain.model;

import java.util.ArrayList;
import java.util.List;

public class CircuitLayer {
    private int numberOfGates;
    private int numberOfMeasurements;
    private List<Gate> gates;
    private List<Measurement> measurements;

    public CircuitLayer() {
        this.gates = new ArrayList<>();
        this.measurements = new ArrayList<>();
    }

    public void addGate(Gate gate) {
        this.gates.add(gate);
        this.numberOfGates = this.gates.size();
    }

    public void addMeasurements(Measurement measurement) {
        this.measurements.add(measurement);
        this.numberOfMeasurements = this.measurements.size();
    }

    public int getNumberOfGates() {return numberOfGates;}

    public int getNumberOfMeasurements() {return numberOfMeasurements;}

}

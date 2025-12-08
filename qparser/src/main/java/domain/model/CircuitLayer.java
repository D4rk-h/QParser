package domain.model;

import java.util.ArrayList;
import java.util.List;

public class CircuitLayer {
    private int numberOfGates;
    private List<Gate> gates;

    public CircuitLayer() {
        this.gates = new ArrayList<>();
    }

    private void addGate(Gate gate) {
        this.gates.add(gate);
        this.numberOfGates = this.gates.size();
    }

    public int getNumberOfGates() {return numberOfGates;}

    public List<Gate> getGates() {return gates;}

}

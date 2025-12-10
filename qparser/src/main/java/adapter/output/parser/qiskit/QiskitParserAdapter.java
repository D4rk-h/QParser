package adapter.output.parser.qiskit;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;
import domain.port.output.CircuitParser;

import java.util.ArrayList;
import java.util.List;

public class QiskitParserAdapter implements CircuitParser {
    private final String supportedType = "QISKIT";
    private final QiskitParsingUtils utils = new QiskitParsingUtils();

    public QiskitParserAdapter() {}

    @Override
    public Circuit parseScript(String script) throws ParsingException {
        int nQubits = 0;
        int nClBits = 0;
        String[] lines = script.split("\n");
        List<CircuitLayer> layers = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.contains("QuantumRegister(")) nQubits = Integer.parseInt(String.valueOf(line.split(" ")[2].charAt(16)));
            if (line.contains("ClassicalRegister(")) nClBits = Integer.parseInt(String.valueOf(line.split(" ")[2].charAt(18)));
            CircuitLayer layer = utils.processLine(line);
            if (layer != null) {
                layers.add(layer);
            }
        }
        return new Circuit(nQubits, nClBits, layers);
    }

    @Override
    public String parseObject(Circuit circuit) throws ParsingException {
        int nQubits = circuit.numberOfQubits();
        int nClBits = circuit.numberOfClBits();
        StringBuilder sb = new StringBuilder();
        sb.append("from qiskit import QuantumCircuit, QuantumRegister, ClassicalRegister\n");
        for (CircuitLayer layer: circuit.layers()) {
            if (!layer.getGates().isEmpty()) {
                for (Gate gate: layer.getGates()) {
                    if (gate.name().equals("rc3x")) sb.append("from qiskit.circuit.library import RC3XGate\n");
                }
            }
        }
        sb.append("from numpy import pi\n\n");
        sb.append("qreg_q = QuantumRegister(").append(nQubits).append(")\n");
        sb.append("creg_c = ClassicalRegister(").append(nClBits).append(")");
        sb.append("circuit = QuantumCircuit(qreg_q, creg_c)\n\n");

        for (CircuitLayer layer: circuit.layers()) {
            for (Gate gate: layer.getGates()) {
                sb.append("circuit.");
                if (gate.name().equals("rc3x")) {
                    sb.append("append(RC3XGate(), ");
                    for (int qubit: gate.targetQubits()) {
                        sb.append("[");
                        sb.append("qreg_q[").append(qubit).append("]");
                        if (qubit != gate.targetQubits()[gate.targetQubits().length - 1]) {
                            sb.append(", ");
                        }
                    }
                    sb.append("])\n");
                    continue;
                }
                sb.append(gate.name()).append("(");
                if (gate.parameters().length > 0) {
                    sb.append("(");
                }
                List<String> params = new ArrayList<>();
                for (String param: gate.parameters()) {
                    params.add(param);
                }
                sb.append(String.join(", ", params));
                sb.append(")");
                List<String> controlQubitStrs = new ArrayList<>();
                for (Integer qubit: gate.controlQubits()) {
                    controlQubitStrs.add("qreg_q[" + qubit + "]");
                }
                List<String> targetQubitStrs = new ArrayList<>();
                for (Integer qubit: gate.targetQubits()) {
                    targetQubitStrs.add("qreg_q[" + qubit + "]");
                }
                sb.append(", ");
                if (controlQubitStrs.isEmpty()) {
                    sb.append(String.join(", ", targetQubitStrs));
                }
                List<String> allQubits = new ArrayList<>();
                allQubits.addAll(controlQubitStrs);
                allQubits.addAll(targetQubitStrs);
                sb.append(String.join(", ", allQubits));
                sb.append(")\n");
            }
            for (Measurement meas: layer.getMeasurements()) {
                sb.append("circuit.measure(qreg_q[").append(meas.qubitIndex()).append("], creg_c[").append(meas.bitIndex()).append("])\n");
            }
        }
        return sb.toString();
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

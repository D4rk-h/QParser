package adapter.output.parser;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;
import domain.port.output.CircuitParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QasmParserAdapter implements CircuitParser {
    private final String supportedType = "QASM";
    private final String patternParamGates = "^(\\w+)\\(([^)]+)\\)\\s+(.+);?$";
    private final String patternCGates = "^(\\w+)\\s+(.+);?$";
    private final List<String> rotationGates = List.of("rx", "rz", "ry", "u", "rxx", "rzz");
    private final List<String> controlledGates = List.of(
            "cx", "cz", "ch", "ccx", "swap", "c3x", "c4x", "cp", "cswap", "crx", "cry", "crz", "cu");
    private final List<String> oneQubitGates = List.of("h", "x", "y", "z", "s", "sdg", "t", "tdg", "id");

    public QasmParserAdapter() {
    }

    @Override
    public Circuit parse(String script) throws ParsingException {
        //todo; implement a better design to manage circuit layers
        String[] lines = script.split("\n");
        if (lines.length == 0 || !lines[0].contains("OPENQASM")) throw new ParsingException("Wrong script format: Expected qasm");
        int nQubits = 0;
        int nBits = 0;
        List<CircuitLayer> layers = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("//")) continue;

            if (line.contains("qreg")) {
                nQubits = extractNumber(line);
            } else if (line.contains("creg")) {
                nBits = extractNumber(line);
            } else if (line.contains("barrier") || line.contains("OPENQASM") || line.contains("include")) {
                continue;
            } else {
                CircuitLayer layer = processLine(line);
                if (layer != null) {
                    layers.add(layer);
                }
            }
        }
        return new Circuit(nQubits, nBits, layers);
    }

    private CircuitLayer processLine(String line) throws ParsingException {
        CircuitLayer layer = new CircuitLayer();
        if (line.contains("measure")) {
            processMeasurement(line, layer);
            return layer;
        }

        for (String gate : rotationGates) {
            if (line.matches("^" + gate + "\\(.*")) {
                processRotationGate(line, gate, layer);
                return layer;
            }
        }

        for (String gate : controlledGates) {
            if (gate.startsWith("c") && (gate.contains("rx") || gate.contains("ry") || gate.contains("rz") || gate.equals("cu") || gate.equals("cp"))) {
                if (line.matches("^" + gate + "\\(.*")) {
                    processControlledRotationGate(line, gate, layer);
                    return layer;
                }
            }
        }

        for (String gate : controlledGates) {
            if (line.startsWith(gate + " ")) {
                processControlledGate(line, gate, layer);
                return layer;
            }
        }

        for (String gate : oneQubitGates) {
            if (line.startsWith(gate + " ")) {
                processOneQubitGate(line, gate, layer);
                return layer;
            }
        }

        return null;
    }


    private void processMeasurement(String line, CircuitLayer layer) {
        Pattern p = Pattern.compile("measure\\s+q\\[(\\d+)]\\s+->\\s+c\\[(\\d+)]");
        Matcher m = p.matcher(line);
        if (m.find()) {
            int qubitIndex = Integer.parseInt(m.group(1));
            int bitIndex = Integer.parseInt(m.group(2));
            Measurement measurement = new Measurement(qubitIndex, bitIndex);
            layer.addMeasurement(measurement);
            System.out.println("Measurement: qubit[" + qubitIndex + "] -> bit[" + bitIndex + "]");
        }
    }

    private void processRotationGate(String line, String gateName, CircuitLayer layer) {
        Pattern p = Pattern.compile(patternParamGates);
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String params = m.group(2);
            String targets = m.group(3);
            if (gateName.equals("u")) {
                String[] paramArray = params.split(",");
                float r1 = parseParameter(paramArray[0].trim());
                float r2 = parseParameter(paramArray[1].trim());
                float r3 = parseParameter(paramArray[2].trim());
                int[] targetQubits = extractQubits(targets);
                Gate gate = new Gate(gateName, targetQubits, new int[]{},new String[]{String.valueOf(r1), String.valueOf(r2), String.valueOf(r3)});
                layer.addGate(gate);
                System.out.println("Gate: " + gateName + ", targets: " + targetQubits[0] +
                        ", params: [" + r1 + ", " + r2 + ", " + r3 + "]");
            } else if (gateName.equals("rxx") || gateName.equals("rzz")) {
                float rotation = parseParameter(params.trim());
                int[] targetQubits = extractQubits(targets);
                Gate gate = new Gate(gateName, targetQubits, new int[]{},new String[]{String.valueOf(rotation)});
                layer.addGate(gate);
                System.out.println("Gate: " + gateName + ", targets: [" + targetQubits[0] +
                        ", " + targetQubits[1] + "], rotation: " + rotation);
            } else {
                float rotation = parseParameter(params.trim());
                int[] targetQubits = extractQubits(targets);
                Gate gate = new Gate(gateName, targetQubits, new int[]{}, new String[]{String.valueOf(rotation)});
                layer.addGate(gate);
                System.out.println("Gate: " + gateName + ", target: " + targetQubits[0] +
                        ", rotation: " + rotation);
            }
        }
    }

    private void processControlledRotationGate(String line, String gateName, CircuitLayer layer) {
        Pattern p = Pattern.compile("^(\\w+)\\(([^)]+)\\)\\s+(.+);?$");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String params = m.group(2);
            String targets = m.group(3);
            float rotation = parseParameter(params.trim());
            int[] qubits = extractQubits(targets);
            int[] controls = new int[qubits.length - 1];
            System.arraycopy(qubits, 0, controls, 0, controls.length);
            int[] targetQubits = new int[]{qubits[qubits.length - 1]};
            Gate gate = new Gate(gateName, targetQubits, controls, new String[]{String.valueOf(rotation)});
            layer.addGate(gate);
            System.out.println("Gate: " + gateName + ", target: " + targetQubits[0] +
                    ", controls: " + controls[0] + ", rotation: " + rotation);
        }
    }

    private void processControlledGate(String line, String gateName, CircuitLayer layer) {
        Pattern p = Pattern.compile(patternCGates);
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String targets = m.group(2);
            int[] qubits = extractQubits(targets);
            int[] controls = new int[qubits.length - 1];
            System.arraycopy(qubits, 0, controls, 0, controls.length);
            int[] targetQubits = new int[]{qubits[qubits.length - 1]};
            Gate gate = new Gate(gateName, targetQubits, controls, new String[]{});
            layer.addGate(gate);
            System.out.print("Gate: " + gateName + ", target: " + targetQubits[0] + ", controls: [");
            for (int i = 0; i < controls.length; i++) {
                System.out.print(controls[i] + (i < controls.length - 1 ? ", " : ""));
            }
            System.out.println("]");
        }
    }

    private void processOneQubitGate(String line, String gateName, CircuitLayer layer) {
        Pattern p = Pattern.compile(patternCGates);
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String targets = m.group(2);
            int[] targetQubits = extractQubits(targets);
            Gate gate = new Gate(gateName, targetQubits, new int[]{}, new String[]{});
            layer.addGate(gate);
            System.out.println("Gate: " + gateName + ", target: " + targetQubits[0]);
        }
    }

    private float parseParameter(String param) {
        param = param.trim();
        param = param.replace("pi", String.valueOf(Math.PI));
        if (param.contains("/")) {
            String[] parts = param.split("/");
            return Float.parseFloat(parts[0].trim()) / Float.parseFloat(parts[1].trim());
        }
        if (param.contains("*")) {
            String[] parts = param.split("\\*");
            return Float.parseFloat(parts[0].trim()) * Float.parseFloat(parts[1].trim());
        }

        return Float.parseFloat(param);
    }

    private int[] extractQubits(String targets) {
        String[] parts = targets.split(",");
        int[] qubits = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String cleaned = parts[i].trim().replace("q[", "").replace("]", "").replace(";", "");
            qubits[i] = Integer.parseInt(cleaned);
        }
        return qubits;
    }

    private int extractNumber(String line) {
        Pattern p = Pattern.compile("\\[(\\d+)]");
        Matcher m = p.matcher(line);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}
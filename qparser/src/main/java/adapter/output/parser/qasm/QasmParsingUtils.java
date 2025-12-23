package adapter.output.parser.qasm;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QasmParsingUtils {
    private final String patternParamGates = "^(\\w+)\\(([^)]+)\\)\\s+(.+);?$";
    private final String patternCGates = "^(\\w+)\\s+(.+);?$";
    CircuitLayer processLine(String line, List<String> rotationGates,
                             List<String> controlledGates, List<String> oneQubitGates) throws ParsingException {
        CircuitLayer layer = new CircuitLayer();
        if (line.contains("measure")) {
            processMeasurement(line, layer);
            return layer;
        }

        for (String gate : rotationGates) {
            if (line.matches("^" + gate + "\\(.*")) {
                processRotationGate(line, gate, layer, patternParamGates);
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
                processControlledGate(line, gate, layer, patternCGates);
                return layer;
            }
        }

        for (String gate : oneQubitGates) {
            if (line.startsWith(gate + " ")) {
                processOneQubitGate(line, gate, layer, patternCGates);
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

    private void processRotationGate(String line, String gateName, CircuitLayer layer, String patternParamGates) {
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
            String[] paramArray = params.split(",");
            List<String> rotations = new ArrayList<>();
            for (String param : paramArray) {
                rotations.add(String.valueOf(parseParameter(param.trim())));
            }
            int[] qubits = extractQubits(targets);
            int[] controls = new int[qubits.length - 1];
            System.arraycopy(qubits, 0, controls, 0, controls.length);
            int[] targetQubits = new int[]{qubits[qubits.length - 1]};
            Gate gate = new Gate(gateName, targetQubits, controls, rotations.toArray(new String[0]));
            layer.addGate(gate);
        }
    }

    private void processControlledGate(String line, String gateName, CircuitLayer layer, String patternCGates) {
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

    private void processOneQubitGate(String line, String gateName, CircuitLayer layer, String patternCGates) {
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

    int extractNumber(String line) {
        Pattern p = Pattern.compile("\\[(\\d+)]");
        Matcher m = p.matcher(line);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    String parseObject(Circuit circuit, int nQubits, int nClBits) {
        StringBuilder sb = new StringBuilder();
        sb.append("OPENQASM 2.0;\n");
        sb.append("include \"qelib1.inc\";\n\n");
        sb.append("qreg q[").append(nQubits).append("];\n");
        sb.append("creg c[").append(nClBits).append("];\n");

        for (CircuitLayer layer: circuit.layers()) {
            for (Gate gate: layer.getGates()) {
                sb.append("circuit.");
                sb.append(gate.name()).append("(");
                if (gate.parameters().length > 0) {
                    sb.append("(");
                }
                List<String> params = new ArrayList<>();
                for (String param: gate.parameters()) params.add(param);
                sb.append(String.join(", ", params));
                sb.append(")");
                List<String> controlQubitStrs = new ArrayList<>();
                for (Integer qubit: gate.controlQubits()) {
                    controlQubitStrs.add("qreg q[" + qubit + "]");
                }
                List<String> targetQubitStrs = new ArrayList<>();
                for (Integer qubit: gate.targetQubits()) {
                    targetQubitStrs.add("qreg q[" + qubit + "]");
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
                sb.append("measure q[").append(meas.qubitIndex()).append("] -> c[").append(meas.bitIndex()).append("];\n");
            }
        }
        return sb.toString();
    }
}
package adapter.output.parser.qiskit;

import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;
 import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QiskitParsingUtils {
    private final Pattern GATE_PATTERN = Pattern.compile("circuit\\.(\\w+)\\((.*)\\)");
    private final Pattern QUBIT_PATTERN = Pattern.compile("qreg_q\\[(\\d+)\\]");
    private final Pattern CLBIT_PATTERN = Pattern.compile("creg_c\\[(\\d+)\\]");

    CircuitLayer processLine(String line) {
        CircuitLayer layer = new CircuitLayer();
        if (line.isEmpty() || line.startsWith("#") || line.startsWith("from") ||
                line.contains("QuantumRegister") || line.contains("ClassicalRegister") ||
                line.contains("QuantumCircuit")) {
            return null;
        }
        if (line.contains(".append(")) {
            String[] split = line.split(", ");
            int target1 = Integer.parseInt(String.valueOf(split[1].charAt(8)));
            int target2 = Integer.parseInt(String.valueOf(split[2].charAt(7)));
            int target3 = Integer.parseInt(String.valueOf(split[3].charAt(7)));
            int target4 = Integer.parseInt(String.valueOf(split[4].charAt(7)));
            Gate gate = new Gate("rc3x", new int[]{target1,target2,target3,target4}, new int[]{}, new String[]{});
            layer.addGate(gate);
            System.out.println(String.format("Gate: rc3x, targets: [%d, %d, %d, %d]", target1, target2, target3, target4));
            return layer;
        }
        Matcher gateMatcher = GATE_PATTERN.matcher(line);
        if (!gateMatcher.find()) return null;
        String gateName = gateMatcher.group(1);
        String args = gateMatcher.group(2);
        if (gateName.equals("measure")) {
            Matcher qubitMatcher = QUBIT_PATTERN.matcher(args);
            Matcher clbitMatcher = CLBIT_PATTERN.matcher(args);
            if (qubitMatcher.find() && clbitMatcher.find()) {
                int qubitIndex = Integer.parseInt(qubitMatcher.group(1));
                int bitIndex = Integer.parseInt(clbitMatcher.group(1));
                layer.addMeasurement(new Measurement(qubitIndex, bitIndex));
                System.out.println("Measurement: qubit[" + qubitIndex + "] -> bit[" + bitIndex + "]");
                return layer;
            }
            return null;
        }
        List<Integer> qubits = new ArrayList<>();
        Matcher qubitMatcher = QUBIT_PATTERN.matcher(args);
        while (qubitMatcher.find()) {
            qubits.add(Integer.parseInt(qubitMatcher.group(1)));
        }

        if (qubits.isEmpty()) {
            return null;
        }
        List<String> params = extractParameters(args);
        Gate gate = createGate(gateName, qubits, params);
        layer.addGate(gate);
        printGateInfo(gate);
        return layer;
    }

    private List<String> extractParameters(String args) {
        List<String> params = new ArrayList<>();
        String cleaned = args.replaceAll("qreg_q\\[\\d+\\]", "").trim();
        String[] parts = cleaned.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && isNumericParam(trimmed)) {
                if (parseParameter(trimmed) != 0f) {
                    params.add(String.valueOf(parseParameter(trimmed)));
                }
            }
        }
        return params;
    }

    private boolean isNumericParam(String param) {
        return param.matches("[\\d\\.\\+\\-\\*/\\s]+") ||
                param.contains("pi") ||
                param.matches("-?\\d+(\\.\\d+)?");
    }

    private Gate createGate(String gateName, List<Integer> qubits, List<String> params) {
        int[] targetQubits;
        int[] controlQubits;
        String[] parameters = params.toArray(new String[0]);
        if (isControlledGate(gateName)) {
            int numControls = getNumControls(gateName);
            controlQubits = new int[Math.min(numControls, qubits.size() - 1)];
            for (int i = 0; i < controlQubits.length; i++) controlQubits[i] = qubits.get(i);
            targetQubits = new int[qubits.size() - controlQubits.length];
            for (int i = 0; i < targetQubits.length; i++) targetQubits[i] = qubits.get(controlQubits.length + i);
        } else if (isTwoQubitSymmetric(gateName)) {
            targetQubits = qubits.stream().mapToInt(i -> i).toArray();
            controlQubits = new int[]{};
        } else {
            targetQubits = qubits.stream().mapToInt(i -> i).toArray();
            controlQubits = new int[]{};
        }
        return new Gate(gateName, targetQubits, controlQubits, parameters);
    }

    private boolean isControlledGate(String gateName) {
        return gateName.startsWith("c") && !gateName.equals("creg") &&
                (gateName.equals("cx") || gateName.equals("cy") || gateName.equals("cz") ||
                        gateName.equals("ch") || gateName.equals("cu") || gateName.equals("ccx") ||
                        gateName.equals("csx") || gateName.equals("rccx") || gateName.startsWith("c"));
    }

    private int getNumControls(String gateName) {
        if (gateName.equals("ccx") || gateName.equals("rccx")) return 2;
        if (gateName.startsWith("c") && !gateName.startsWith("cc")) return 1;
        return 1;
    }

    private boolean isTwoQubitSymmetric(String gateName) {
        return gateName.equals("swap") || gateName.equals("iswap") ||
                gateName.equals("rxx") || gateName.equals("ryy") || gateName.equals("rzz");
    }

    private float parseParameter(String param) {
        param = param.trim().replace("pi", String.valueOf(Math.PI));
        try {
            if (param.contains("/")) {
                String[] parts = param.split("/");
                return Float.parseFloat(parts[0].trim()) / Float.parseFloat(parts[1].trim());
            }
            if (param.contains("*")) {
                String[] parts = param.split("\\*");
                return Float.parseFloat(parts[0].trim()) * Float.parseFloat(parts[1].trim());
            }
            return Float.parseFloat(param);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    private void printGateInfo(Gate gate) {
        StringBuilder sb = new StringBuilder("Gate: " + gate.name());
        if (gate.targetQubits().length > 0) {
            sb.append(", targets: ").append(arrayToString(gate.targetQubits()));
        }
        if (gate.controlQubits().length > 0) {
            sb.append(", controls: ").append(arrayToString(gate.controlQubits()));
        }
        if (gate.parameters().length > 0) {
            sb.append(", params: ").append(java.util.Arrays.toString(gate.parameters()));
        }
        System.out.println(sb);
    }

    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    String parseObject(Circuit circuit, int nQubits, int nClBits) {
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
                for (String param: gate.parameters()) params.add(param);
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
}

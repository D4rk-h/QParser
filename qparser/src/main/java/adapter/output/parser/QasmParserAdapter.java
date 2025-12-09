package adapter.output.parser;

import domain.exception.ParsingException;
import domain.model.Circuit;
import domain.model.CircuitLayer;
import domain.model.Gate;
import domain.model.Measurement;
import domain.port.output.CircuitParser;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

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
        List<String> splitScript = List.of(script.split("\n"));
        if (!splitScript.getFirst().contains("OPENQASM")) throw new ParsingException("Wrong script format: Expected qasm");
        splitScript.addAll(Arrays.asList(script.split("\n")));
        for (String line: splitScript) {
            if (line.contains("qreg")) {
                int nQubits = line.charAt(7);
            } else if(line.contains("creg")) {
                int nBits = line.charAt(7);
            } else if(line.contains("barrier")) {
                // skip barrier
            } else {
                CircuitLayer layer = new CircuitLayer();
                if( line.contains("measure")) {
                    int qubitIndex = line.charAt(10);
                    int bitIndex = line.charAt(18);
                    Measurement m = new Measurement(qubitIndex, bitIndex);
                    layer.addMeasurement(m);
                }
                for (String gate: rotationGates) {
                    if (line.contains(gate)) {
                        Pattern p = Pattern.compile(this.patternParamGates);
                        Matcher m = p.matcher(line);
                        if (m.matches()) {
                            String gateName = m.group(1);
                            String params = m.group(2);
                            String targets = m.group(3);
                            if (gateName.equals("u")) {
                                String r1 = params.split(",")[0].trim().replace("pi", String.valueOf(Math.PI));
                                String r2 = params.split(",")[1].trim().replace("pi", String.valueOf(Math.PI));
                                String r3 = params.split(",")[2].trim().replace("pi", String.valueOf(Math.PI));
                                Gate ug = new Gate(
                                        gateName,
                                        new int[]{targets.charAt(2)},
                                        new int[]{},
                                        new String[]{r1, r2, r3}
                                );
                                layer.addGate(ug);
                            } else {
                                String r = params.trim().replace("pi", String.valueOf(Math.PI));
                                if(gateName.equals("rxx") || gateName.equals("rzz")) {
                                    Gate rg = new Gate(gateName,
                                            new int[]{targets.charAt(2), targets.charAt(8)}, new int[]{}, new String[]{r});
                                    layer.addGate(rg);
                                } else {
                                    Gate rg = new Gate(gateName,
                                            new int[]{targets.charAt(2)}, new int[]{}, new String[]{r});
                                    layer.addGate(rg);
                                }
                            }
                        }
                    }
                }
                for (String gate: oneQubitGates) {
                    if (line.contains(gate)) {
                        String gateName = String.valueOf(line.charAt(0));
                        String targets = String.valueOf(line.charAt(4));
                        Gate g = new Gate(gateName, new int[]{targets.charAt(2)}, new int[]{}, new String[]{});
                        layer.addGate(g);
                    }
                }
                for (String gate: controlledGates) {
                    if (line.contains(gate)) {
                        Pattern p = Pattern.compile(patternCGates);
                        Matcher m = p.matcher(line);
                        if (m.matches()) {
                            String gateName = m.group(1);
                            String targets = m.group(2);
                            String[] targetQubitsUnformatted = targets.split(",");
                            for (int i = 0; i < targetQubitsUnformatted.length; i++) {
                                targetQubitsUnformatted[i] = targetQubitsUnformatted[i].trim().replace("q[", "").replace("]", "").replace(";", "");
                            }
                            String[] controlQubitsUnformatted = new String[targetQubitsUnformatted.length];
                            for (int i = 0; i < controlQubitsUnformatted.length - 1; i++) {
                                controlQubitsUnformatted[i] = targetQubitsUnformatted[i];
                                System.out.println(controlQubitsUnformatted[i]);
                            }

                            Gate cg = new Gate(
                                    gateName,
                                    new int[]{Integer.valueOf(targetQubitsUnformatted[targetQubitsUnformatted.length - 1])},
                                    new int[]{Integer.valueOf(controlQubitsUnformatted[0])},
                                    new String[]{}
                            );
                            layer.addGate(cg);
                        }
                    }
                }
            }
        }
        return null;
    }

    public String getSupportedType() {
        return this.supportedType;
    }
}

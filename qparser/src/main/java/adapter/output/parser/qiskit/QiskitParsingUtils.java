package adapter.output.parser.qiskit;

import java.util.regex.Pattern;

class QiskitParsingUtils {
    private final Pattern SINGLE_QUBIT = Pattern.compile("circuit\\.(\\w+)\\(qreg_q\\[(\\d+)\\]\\)");
    private final Pattern ROTATION_GATE = Pattern.compile("circuit\\.(r\\w+)\\(([^,]+),\\s*qreg_q\\[(\\d+)\\](?:,\\s*qreg_q\\[(\\d+)\\])?\\)");
    private final Pattern CONTROLLED_GATE = Pattern.compile("circuit\\.(c+\\w+)\\((.+?)\\)");
    private final Pattern MEASUREMENT = Pattern.compile("circuit\\.measure\\(qreg_q\\[(\\d+)\\],\\s*creg_c\\[(\\d+)\\]\\)");



}

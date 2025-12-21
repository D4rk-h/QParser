package adapter.input.rest.dto;

import java.util.Arrays;
import java.util.List;

public record GateDto(
        String name,
        List<Integer> targetQubits,
        List<Integer> controlQubits,
        List<String> parameters
) {
    public GateDto(String name, int[] targetQubits, int[] controlQubits, String[] parameters) {
        this(
                name,
                targetQubits != null ? Arrays.stream(targetQubits).boxed().toList() : List.of(),
                controlQubits != null ? Arrays.stream(controlQubits).boxed().toList() : List.of(),
                parameters != null ? Arrays.asList(parameters) : List.of()
        );
    }
}


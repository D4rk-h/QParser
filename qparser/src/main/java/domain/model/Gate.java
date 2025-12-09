package domain.model;

public record Gate(
    String name,
    int[] targetQubits,
    int[] controlQubits,
    String[] parameters
)
{}

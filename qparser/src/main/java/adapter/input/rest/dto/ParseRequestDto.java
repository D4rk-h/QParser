package adapter.input.rest.dto;

public record ParseRequestDto (
    String script,
    String scriptType,
    String desiredType
)
{ }

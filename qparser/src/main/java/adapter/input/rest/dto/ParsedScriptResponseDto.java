package adapter.input.rest.dto;

public record ParsedScriptResponseDto(
        String parsedScript,
        String scriptType
) {}
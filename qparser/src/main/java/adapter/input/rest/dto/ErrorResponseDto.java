package adapter.input.rest.dto;

public record ErrorResponseDto (
        String error,
        String message
)
{}
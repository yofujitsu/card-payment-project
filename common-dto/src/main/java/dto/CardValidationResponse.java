package dto;

public record CardValidationResponse(
        boolean valid,
        String reason
) {}


package dto;

public record CardValidationRequest(
        String cardNumber,
        String expiryDate,
        String cvv
) {}


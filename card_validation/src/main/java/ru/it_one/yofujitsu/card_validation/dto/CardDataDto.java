package ru.it_one.yofujitsu.card_validation.dto;

import jakarta.validation.constraints.NotBlank;

public record CardDataDto(
        @NotBlank(message = "Номер карты обязателен для ввода.")
        String cardNumber,

        @NotBlank(message = "Срок истечения карты обязателен для ввода.")
        String expiryDate,

        @NotBlank(message = "CVV обязателен для ввода.")
        String cvv
) {}
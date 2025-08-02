package ru.it_one.yofujitsu.card_validation.dto;

public record ValidationResultDto(
        boolean valid,
        String message
) {}
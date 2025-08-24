package ru.yofujitsu.payment_processing.exception;

public class BankAuthResponseException extends RuntimeException {
    public BankAuthResponseException(String message) {
        super(message);
    }
}

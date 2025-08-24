package ru.it_one.yofujitsu.bank_payment.mapper;

import org.springframework.stereotype.Component;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankAuthorizationRequest;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankAuthorizationResponseDto;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.BankResponseDto;
import ru.it_one.yofujitsu.bank_payment.dto.bank_auth.PaymentStatus;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;

import java.util.UUID;

@Component
public class BankPaymentMapper {

    public BankRequest toBankRequestApi(BankAuthorizationRequest bankRequest) {
        BankRequest request = new BankRequest();
        request.setAmount(bankRequest.amount());
        request.setCurrency(bankRequest.currency());
        request.setCvv(bankRequest.cvv());
        request.setCardNumber(bankRequest.cardNumber());
        request.setExpiryDate(bankRequest.expiryDate());
        return request;
    }

    public BankResponseDto toBankResponseDto(BankResponse bankResponse) {
        return new BankResponseDto(
                UUID.fromString(bankResponse.getBankTransactionId()),
                bankResponse.getApproved() ? PaymentStatus.APPROVED : PaymentStatus.REJECTED,
                bankResponse.getReason()
        );
    }

    public BankAuthorizationResponseDto toBankAuthorizationResponseDto(BankAuthorizationRequest bankAuthorizationRequest, BankResponseDto bankResponse) {
        return new BankAuthorizationResponseDto(
                UUID.fromString(bankAuthorizationRequest.transactionId()),
                bankResponse,
                bankAuthorizationRequest.cardNumber(),
                bankAuthorizationRequest.expiryDate(),
                bankAuthorizationRequest.cvv(),
                bankAuthorizationRequest.amount(),
                bankAuthorizationRequest.currency(),
                bankAuthorizationRequest.email()
        );
    }
}

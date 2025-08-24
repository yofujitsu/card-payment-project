package ru.yofujitsu.payment_processing.mapper;

import org.springframework.stereotype.Component;
import ru.yofujitsu.payment_processing.dto.FinalTransactionStatusDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.BankAuthorizationResponseDto;
import ru.yofujitsu.payment_processing.dto.bank_gateway.TransactionStatusDto;
import ru.yofujitsu.payment_processing.dto.notification.NotificationDto;
import ru.yofujitsu.payment_processing.model.FinalTransactionStatus;
import ru.yofujitsu.payment_processing.model.TransactionStatus;

@Component
public class BankPaymentMapper {

    public FinalTransactionStatusDto toFinalTransactionStatusDto(FinalTransactionStatus finalTransactionStatus, BankAuthorizationResponseDto bankResponseDto) {
        return new FinalTransactionStatusDto(
                finalTransactionStatus.getTransactionId(),
                finalTransactionStatus.getStatus().equals(TransactionStatus.SUCCESS)
                        ? TransactionStatusDto.SUCCESS : TransactionStatusDto.FAILED,
                bankResponseDto.bankResponse(),
                bankResponseDto.amount(),
                bankResponseDto.currency()
        );
    }

    public NotificationDto toNotificationDto(BankAuthorizationResponseDto dto) {
        return new NotificationDto(
                String.valueOf(dto.transactionId()),
                dto.bankResponse().status(),
                dto.email()
        );
    }
}

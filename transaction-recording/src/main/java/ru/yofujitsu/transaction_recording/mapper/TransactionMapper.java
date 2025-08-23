package ru.yofujitsu.transaction_recording.mapper;

import org.springframework.stereotype.Component;
import ru.yofujitsu.transaction_recording.dto.FinalTransactionStatusDto;
import ru.yofujitsu.transaction_recording.dto.TransactionStatusDto;
import ru.yofujitsu.transaction_recording.entity.BankResponseEntity;
import ru.yofujitsu.transaction_recording.entity.TransactionEntity;
import ru.yofujitsu.transaction_recording.model.TransactionRecord;
import ru.yofujitsu.transaction_recording.model.TransactionRecordBankResponse;
import ru.yofujitsu.transaction_recording.model.TransactionStatus;

@Component
public class TransactionMapper {

    public TransactionRecord toTransactionRecord(TransactionEntity entity) {
        if (entity == null) {
            return null;
        }

        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(entity.getTransactionId());
        record.setAmount(entity.getAmount());
        record.setCurrency(entity.getCurrency());
        record.setStatus(entity.getStatus() != null ? TransactionStatus.valueOf(entity.getStatus().name()) : null);
        record.setCreatedAt(entity.getCreatedAt());

        if (entity.getBankResponse() != null) {
            TransactionRecordBankResponse bankResponse = new TransactionRecordBankResponse();
            bankResponse.setBankTransactionId(entity.getBankResponse().getBankTransactionId());
            bankResponse.setReason(entity.getBankResponse().getReason());
            record.setBankResponse(bankResponse);
        }

        return record;
    }

    public TransactionEntity toTransactionEntity(TransactionRecord record) {
        if (record == null) {
            return null;
        }

        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(record.getTransactionId());
        entity.setAmount(record.getAmount());
        entity.setCurrency(record.getCurrency());
        entity.setStatus(record.getStatus() != null ? TransactionStatusDto.valueOf(record.getStatus().name()) : null);
        entity.setCreatedAt(record.getCreatedAt());

        if (record.getBankResponse() != null) {
            BankResponseEntity bankResponse = new BankResponseEntity();
            bankResponse.setBankTransactionId(record.getBankResponse().getBankTransactionId());
            bankResponse.setReason(record.getBankResponse().getReason());
            entity.setBankResponse(bankResponse);
        }

        return entity;
    }

    public TransactionRecord toTransactionRecord(FinalTransactionStatusDto dto) {
        if (dto == null) {
            return null;
        }

        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(dto.transactionId());
        record.setAmount(dto.amount());
        record.setCurrency(dto.currency());
        record.setStatus(dto.status() != null ? TransactionStatus.valueOf(dto.status().name()) : null);
        record.setCreatedAt(null);

        if (dto.bankResponse() != null) {
            TransactionRecordBankResponse bankResponse = new TransactionRecordBankResponse();
            bankResponse.setBankTransactionId(dto.bankResponse().bankTransactionId() != null ? dto.bankResponse().bankTransactionId().toString() : null);
            bankResponse.setReason(dto.bankResponse().message());
            record.setBankResponse(bankResponse);
        }

        return record;
    }
}
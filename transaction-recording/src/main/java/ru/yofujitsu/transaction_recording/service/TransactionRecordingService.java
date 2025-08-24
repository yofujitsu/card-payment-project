package ru.yofujitsu.transaction_recording.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yofujitsu.transaction_recording.dto.log_entry.LogLevel;
import ru.yofujitsu.transaction_recording.entity.TransactionEntity;
import ru.yofujitsu.transaction_recording.exception.TransactionNotFoundException;
import ru.yofujitsu.transaction_recording.mapper.TransactionMapper;
import ru.yofujitsu.transaction_recording.model.TransactionRecord;
import ru.yofujitsu.transaction_recording.rabbit.LogMessageProducer;
import ru.yofujitsu.transaction_recording.repository.TransactionRepository;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRecordingService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final LogMessageProducer logMessageProducer;

    public TransactionRecord getTransactionById(String transactionId) {
        try {
            TransactionEntity transactionEntity = transactionRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new TransactionNotFoundException("Транзакция с ID: %s не найдена."
                            .formatted(transactionId)));
            return transactionMapper.toTransactionRecord(transactionEntity);
        } catch (TransactionNotFoundException e) {
            logMessageProducer.produce(LogLevel.ERROR, "Транзакция с ID: %s не найдена.".formatted(transactionId));
        }
        return null;
    }

    public void saveTransactionRecord(TransactionRecord transactionRecord) {
        transactionRecord.setCreatedAt(OffsetDateTime.now());
        transactionRepository.save(transactionMapper.toTransactionEntity(transactionRecord));
        logMessageProducer.produce(LogLevel.INFO, "Сохранена транзакция с ID: %s"
                .formatted(transactionRecord.getTransactionId()));
    }
}

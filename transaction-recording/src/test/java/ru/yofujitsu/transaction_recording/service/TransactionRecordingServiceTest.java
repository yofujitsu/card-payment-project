package ru.yofujitsu.transaction_recording.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yofujitsu.transaction_recording.dto.TransactionStatusDto;
import ru.yofujitsu.transaction_recording.dto.log_entry.LogLevel;
import ru.yofujitsu.transaction_recording.entity.BankResponseEntity;
import ru.yofujitsu.transaction_recording.entity.TransactionEntity;
import ru.yofujitsu.transaction_recording.mapper.TransactionMapper;
import ru.yofujitsu.transaction_recording.model.*;
import ru.yofujitsu.transaction_recording.rabbit.LogMessageProducer;
import ru.yofujitsu.transaction_recording.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRecordingServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private LogMessageProducer logMessageProducer;

    @InjectMocks
    private TransactionRecordingService transactionRecordingService;

    /**
     * Проверяет получение транзакции по transactionId с валидными данными.
     * Убеждается, что возвращается корректный TransactionRecord и логгер не вызывается.
     */
    @Test
    void testGetTransactionById_Success() {
        // Arrange
        String transactionId = UUID.randomUUID().toString();
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(transactionId);
        entity.setAmount(new BigDecimal("100.0"));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatusDto.SUCCESS);
        entity.setCreatedAt(OffsetDateTime.now());
        BankResponseEntity bankResponse = new BankResponseEntity();
        bankResponse.setBankTransactionId(UUID.randomUUID().toString());
        bankResponse.setReason("Success");
        entity.setBankResponse(bankResponse);

        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(transactionId);
        record.setAmount(new BigDecimal("100.0"));
        record.setCurrency("USD");
        record.setStatus(TransactionStatus.SUCCESS);
        record.setCreatedAt(entity.getCreatedAt());
        TransactionRecordBankResponse recordBankResponse = new TransactionRecordBankResponse();
        recordBankResponse.setBankTransactionId(bankResponse.getBankTransactionId());
        recordBankResponse.setReason("Success");
        record.setBankResponse(recordBankResponse);

        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(entity));
        when(transactionMapper.toTransactionRecord(entity)).thenReturn(record);

        TransactionRecord result = transactionRecordingService.getTransactionById(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        assertEquals(new BigDecimal("100.0"), result.getAmount());
        assertEquals("USD", result.getCurrency());
        assertEquals(TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(entity.getCreatedAt(), result.getCreatedAt());
        assertEquals(bankResponse.getBankTransactionId(), result.getBankResponse().getBankTransactionId());
        assertEquals("Success", result.getBankResponse().getReason());
        verify(logMessageProducer, never()).produce(any(), anyString());
    }

    /**
     * Проверяет получение транзакции по transactionId, когда транзакция не найдена.
     * Убеждается, что возвращается null и вызывается логгер с уровнем ERROR.
     */
    @Test
    void testGetTransactionById_NotFound() {
        String transactionId = UUID.randomUUID().toString();
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        TransactionRecord result = transactionRecordingService.getTransactionById(transactionId);

        assertNull(result);
        verify(logMessageProducer).produce(eq(LogLevel.ERROR), eq("Транзакция с ID: %s не найдена.".formatted(transactionId)));
    }

    /**
     * Проверяет сохранение транзакции с валидными данными.
     * Убеждается, что createdAt устанавливается, маппер и репозиторий вызываются,
     * и логгер записывает сообщение уровня INFO.
     */
    @Test
    void testSaveTransactionRecord_Success() {
        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(UUID.randomUUID().toString());
        record.setAmount(new BigDecimal("100.0"));
        record.setCurrency("USD");
        record.setStatus(TransactionStatus.SUCCESS);
        record.setCreatedAt(null);
        TransactionRecordBankResponse recordBankResponse = new TransactionRecordBankResponse();
        recordBankResponse.setBankTransactionId(UUID.randomUUID().toString());
        recordBankResponse.setReason("Success");
        record.setBankResponse(recordBankResponse);

        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(record.getTransactionId());
        entity.setAmount(new BigDecimal("100.0"));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatusDto.SUCCESS);
        entity.setCreatedAt(OffsetDateTime.now());
        BankResponseEntity bankResponse = new BankResponseEntity();
        bankResponse.setBankTransactionId(recordBankResponse.getBankTransactionId());
        bankResponse.setReason("Success");
        entity.setBankResponse(bankResponse);

        when(transactionMapper.toTransactionEntity(record)).thenReturn(entity);
        when(transactionRepository.save(entity)).thenReturn(entity);

        transactionRecordingService.saveTransactionRecord(record);

        assertNotNull(record.getCreatedAt());
        verify(transactionMapper).toTransactionEntity(record);
        verify(transactionRepository).save(entity);
        verify(logMessageProducer).produce(eq(LogLevel.INFO), eq("Сохранена транзакция с ID: %s".formatted(record.getTransactionId())));
    }
}

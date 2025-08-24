package ru.yofujitsu.transaction_recording.mapper;

import org.junit.jupiter.api.Test;
import ru.yofujitsu.transaction_recording.dto.BankResponseDto;
import ru.yofujitsu.transaction_recording.dto.FinalTransactionStatusDto;
import ru.yofujitsu.transaction_recording.dto.PaymentStatus;
import ru.yofujitsu.transaction_recording.dto.TransactionStatusDto;
import ru.yofujitsu.transaction_recording.entity.BankResponseEntity;
import ru.yofujitsu.transaction_recording.entity.TransactionEntity;
import ru.yofujitsu.transaction_recording.model.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = new TransactionMapper();

    /**
     * Проверяет маппинг из TransactionEntity в TransactionRecord с валидными данными.
     * Убеждается, что все поля, включая bankResponse, корректно маппятся.
     */
    @Test
    void testToTransactionRecordFromEntity_Success() {

        TransactionEntity entity = new TransactionEntity();
        entity.setDatabaseId(UUID.randomUUID());
        entity.setTransactionId(UUID.randomUUID().toString());
        entity.setAmount(new BigDecimal("100.0"));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatusDto.SUCCESS);
        entity.setCreatedAt(OffsetDateTime.now());
        BankResponseEntity bankResponse = new BankResponseEntity();
        bankResponse.setBankTransactionId(UUID.randomUUID().toString());
        bankResponse.setReason("Success");
        entity.setBankResponse(bankResponse);

        TransactionRecord record = transactionMapper.toTransactionRecord(entity);

        assertNotNull(record);
        assertEquals(entity.getTransactionId(), record.getTransactionId());
        assertEquals(new BigDecimal("100.0"), record.getAmount());
        assertEquals("USD", record.getCurrency());
        assertEquals(TransactionStatus.SUCCESS, record.getStatus());
        assertEquals(entity.getCreatedAt(), record.getCreatedAt());
        assertNotNull(record.getBankResponse());
        assertEquals(entity.getBankResponse().getBankTransactionId(), record.getBankResponse().getBankTransactionId());
        assertEquals("Success", record.getBankResponse().getReason());
    }


    /**
     * Проверяет маппинг из TransactionEntity в TransactionRecord с null bankResponse.
     * Убеждается, что поле bankResponse в результате равно null.
     */
    @Test
    void testToTransactionRecordFromEntity_NullBankResponse() {

        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(UUID.randomUUID().toString());
        entity.setAmount(new BigDecimal("100.0"));
        entity.setCurrency("USD");
        entity.setStatus(TransactionStatusDto.SUCCESS);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setBankResponse(null);

        TransactionRecord record = transactionMapper.toTransactionRecord(entity);

        assertNotNull(record);
        assertNull(record.getBankResponse());
    }

    /**
     * Проверяет маппинг из TransactionRecord в TransactionEntity с валидными данными.
     * Убеждается, что все поля, включая bankResponse, корректно маппятся.
     */
    @Test
    void testToTransactionEntity_Success() {

        TransactionRecord record = new TransactionRecord();
        record.setTransactionId(UUID.randomUUID().toString());
        record.setAmount(new BigDecimal("100.0"));
        record.setCurrency("USD");
        record.setStatus(TransactionStatus.SUCCESS);
        record.setCreatedAt(OffsetDateTime.now());
        TransactionRecordBankResponse bankResponse = new TransactionRecordBankResponse();
        bankResponse.setBankTransactionId(UUID.randomUUID().toString());
        bankResponse.setReason("Success");
        record.setBankResponse(bankResponse);

        TransactionEntity entity = transactionMapper.toTransactionEntity(record);

        assertNotNull(entity);
        assertEquals(record.getTransactionId(), entity.getTransactionId());
        assertEquals(new BigDecimal("100.0"), entity.getAmount());
        assertEquals("USD", entity.getCurrency());
        assertEquals(TransactionStatusDto.SUCCESS, entity.getStatus());
        assertEquals(record.getCreatedAt(), entity.getCreatedAt());
        assertNotNull(entity.getBankResponse());
        assertEquals(record.getBankResponse().getBankTransactionId(), entity.getBankResponse().getBankTransactionId());
        assertEquals("Success", entity.getBankResponse().getReason());
    }


    /**
     * Проверяет маппинг из FinalTransactionStatusDto в TransactionRecord с валидными данными.
     * Убеждается, что все поля, включая bankResponse, корректно маппятся, а createdAt равно null.
     */
    @Test
    void testToTransactionRecordFromDto_Success() {
        FinalTransactionStatusDto dto = new FinalTransactionStatusDto(
                UUID.randomUUID().toString(),
                TransactionStatusDto.FAILED,
                new BankResponseDto(UUID.randomUUID(), PaymentStatus.REJECTED, "Failed"),
                new BigDecimal("50.0"),
                "EUR"
        );

        TransactionRecord record = transactionMapper.toTransactionRecord(dto);

        assertNotNull(record);
        assertEquals(dto.transactionId(), record.getTransactionId());
        assertEquals(new BigDecimal("50.0"), record.getAmount());
        assertEquals("EUR", record.getCurrency());
        assertEquals(TransactionStatus.FAILED, record.getStatus());
        assertNull(record.getCreatedAt());
        assertNotNull(record.getBankResponse());
        assertEquals(dto.bankResponse().bankTransactionId().toString(), record.getBankResponse().getBankTransactionId());
        assertEquals("Failed", record.getBankResponse().getReason());
    }


    /**
     * Проверяет маппинг из FinalTransactionStatusDto в TransactionRecord с null bankResponse.
     * Убеждается, что поле bankResponse в результате равно null.
     */
    @Test
    void testToTransactionRecordFromDto_NullBankResponse() {
        FinalTransactionStatusDto dto = new FinalTransactionStatusDto(
                UUID.randomUUID().toString(),
                TransactionStatusDto.FAILED,
                null,
                new BigDecimal("50.0"),
                "EUR"
        );

        TransactionRecord record = transactionMapper.toTransactionRecord(dto);

        assertNotNull(record);
        assertNull(record.getBankResponse());
    }
}

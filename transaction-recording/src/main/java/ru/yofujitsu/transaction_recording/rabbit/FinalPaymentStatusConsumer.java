package ru.yofujitsu.transaction_recording.rabbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.yofujitsu.transaction_recording.dto.FinalTransactionStatusDto;
import ru.yofujitsu.transaction_recording.mapper.TransactionMapper;
import ru.yofujitsu.transaction_recording.service.TransactionRecordingService;

@Component
@Slf4j
@RequiredArgsConstructor
public class FinalPaymentStatusConsumer {

    private final TransactionRecordingService transactionRecordingService;
    private final TransactionMapper transactionMapper;

    @RabbitListener(queues = "${final-payment-status.queue.name}")
    public void consume(FinalTransactionStatusDto finalTransactionStatusDto) {
        log.info("Получены финальные данные транзакции с ID: {}", finalTransactionStatusDto.transactionId());
        transactionRecordingService.saveTransactionRecord(
                transactionMapper.toTransactionRecord(finalTransactionStatusDto)
        );
    }
}

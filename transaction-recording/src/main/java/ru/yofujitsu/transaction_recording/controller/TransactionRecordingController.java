package ru.yofujitsu.transaction_recording.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yofujitsu.transaction_recording.api.TransactionsApi;
import ru.yofujitsu.transaction_recording.model.TransactionRecord;
import ru.yofujitsu.transaction_recording.service.TransactionRecordingService;

@RestController
@RequiredArgsConstructor
public class TransactionRecordingController implements TransactionsApi {

    private final TransactionRecordingService transactionRecordingService;

    @Override
    public ResponseEntity<TransactionRecord> transactionsGet(
            @NotNull
            @Parameter(name = "transactionId", required = true, in = ParameterIn.QUERY)
            @Valid @RequestParam(value = "transactionId", required = true) String transactionId
    ) {
        return ResponseEntity.ok(transactionRecordingService.getTransactionById(transactionId));
    }

    @Override
    public ResponseEntity<Void> transactionsPost(@Valid @RequestBody TransactionRecord transactionRecord) {
        transactionRecordingService.saveTransactionRecord(transactionRecord);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

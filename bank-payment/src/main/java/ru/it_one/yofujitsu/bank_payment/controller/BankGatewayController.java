package ru.it_one.yofujitsu.bank_payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.it_one.yofujitsu.bank_payment.api.BankApi;
import ru.it_one.yofujitsu.bank_payment.model.BankRequest;
import ru.it_one.yofujitsu.bank_payment.model.BankResponse;
import ru.it_one.yofujitsu.bank_payment.service.BankGatewayService;

@RestController
@RequiredArgsConstructor
public class BankGatewayController implements BankApi {

    private final BankGatewayService bankGatewayService;

    @Override
    public ResponseEntity<BankResponse> bankAuthorizePost(@RequestBody @Valid BankRequest request) {
        return ResponseEntity.ok(bankGatewayService.authorizeRequest(request));
    }
}

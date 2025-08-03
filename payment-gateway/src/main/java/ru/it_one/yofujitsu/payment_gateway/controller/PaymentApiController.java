package ru.it_one.yofujitsu.payment_gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.it_one.yofuijtsu.payment_gateway.api.PaymentApi;
import ru.it_one.yofuijtsu.payment_gateway.model.AuthorizationResult;
import ru.it_one.yofuijtsu.payment_gateway.model.PaymentRequest;
import ru.it_one.yofujitsu.payment_gateway.service.PaymentGatewayService;

@RestController
@RequiredArgsConstructor
public class PaymentApiController implements PaymentApi {

    private final PaymentGatewayService paymentGatewayService;

    @Override
    public ResponseEntity<AuthorizationResult> paymentAuthorizePost(@RequestBody @Valid PaymentRequest request) {
        AuthorizationResult result = paymentGatewayService.processAuthorization(request);
        return ResponseEntity.ok(result);
    }
}

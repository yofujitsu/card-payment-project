package ru.it_one.yofujitsu.payment_gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.it_one.yofuijtsu.payment_gateway.model.PaymentRequest;
import ru.it_one.yofujitsu.payment_gateway.dto.bank_auth.BankAuthorizationRequest;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "cardNumber", source = "request.cardNumber")
    @Mapping(target = "expiryDate", source = "request.expiryDate")
    @Mapping(target = "cvv", source = "request.cvv")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "currency", source = "request.currency")
    @Mapping(target = "merchantId", source = "request.merchantId")
    @Mapping(target = "email", source = "request.email")
    BankAuthorizationRequest toBankAuthorizationRequest(String transactionId, PaymentRequest request);
}

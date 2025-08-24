package ru.it_one.yofujitsu.card_validation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.it_one.yofuijtsu.card_validation.api.CardApi;
import ru.it_one.yofuijtsu.card_validation.model.CardData;
import ru.it_one.yofuijtsu.card_validation.model.ValidationResult;
import ru.it_one.yofujitsu.card_validation.validator.CardDataValidator;

@RestController
@RequiredArgsConstructor
@Tag(name = "Card Auth Controller")
public class CardAuthenticationController implements CardApi {

    private final CardDataValidator cardDataValidator;

    @Override
    public ResponseEntity<ValidationResult> cardValidatePost(@Valid @RequestBody CardData cardData) {
        return ResponseEntity.ok(cardDataValidator.validate(cardData));
    }
}

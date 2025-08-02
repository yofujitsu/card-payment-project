package ru.it_one.yofujitsu.card_validation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.it_one.yofujitsu.card_validation.dto.CardDataDto;
import ru.it_one.yofujitsu.card_validation.dto.ValidationResultDto;
import ru.it_one.yofujitsu.card_validation.handler.ErrorResponse;
import ru.it_one.yofujitsu.card_validation.validator.CardDataValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
@Tag(name = "Card Auth Controller")
public class CardAuthenticationController {

    private final CardDataValidator cardDataValidator;

    @Operation(summary = "Validate card details", responses = {
            @ApiResponse(responseCode = "200", description = "Validation result",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationResultDto.class))
                    ),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @PostMapping("/validate")
    @ResponseStatus(HttpStatus.OK)
    public ValidationResultDto validateCard(@Valid @RequestBody CardDataDto cardDataDto) {
        return cardDataValidator.validate(cardDataDto);
    }
}

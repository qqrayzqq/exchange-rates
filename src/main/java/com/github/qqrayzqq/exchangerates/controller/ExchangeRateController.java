package com.github.qqrayzqq.exchangerates.controller;

import com.github.qqrayzqq.exchangerates.dto.ExchangeRateDTO;
import com.github.qqrayzqq.exchangerates.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Exchange Rates")
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @Operation(
            summary = "Get exchange rates",
            description = "Returns exchange rates from the database or freshly from the Erste API."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exchange rates returned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ExchangeRateDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "503", description = "The Erste API is unavailable or returned no data")
    })
    @GetMapping
    public ResponseEntity<List<ExchangeRateDTO>> getAllExchangeRates(
            @Parameter(description = "true = from DB, false = fresh from Erste API", example = "false")
            @RequestParam Boolean usedb) {
        return ResponseEntity.ok(exchangeRateService.getAllExchangeRates(usedb));
    }
}

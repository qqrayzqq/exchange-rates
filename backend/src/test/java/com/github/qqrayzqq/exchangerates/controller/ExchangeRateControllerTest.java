package com.github.qqrayzqq.exchangerates.controller;

import com.github.qqrayzqq.exchangerates.dto.ExchangeRateDTO;
import com.github.qqrayzqq.exchangerates.exception.ExternalApiException;
import com.github.qqrayzqq.exchangerates.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExchangeRateService exchangeRateService;

    @Test
    void getAllExchangeRates_usedbTrue_returns200WithRates() throws Exception {
        ExchangeRateDTO dto = new ExchangeRateDTO("USD", LocalDateTime.of(2022,4,26,0,0), "Dollar", "USA",
                1.07, 1, 22.0, 23.6, 22.798, 22.228, 23.368, 22.798, 1, 22.725, 1.075);
        when(exchangeRateService.getAllExchangeRates(true)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/exchange-rates").param("usedb", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortName").value("USD"))
                .andExpect(jsonPath("$[0].valMid").value(22.798));
    }

    @Test
    void getAllExchangeRates_missingUsedbParam_returns400() throws Exception {
        mockMvc.perform(get("/api/exchange-rates"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllExchangeRates_whenServiceThrows_returns503() throws Exception {
        when(exchangeRateService.getAllExchangeRates(false)).thenThrow(new ExternalApiException("failed", null));

        mockMvc.perform(get("/api/exchange-rates").param("usedb", "false"))
                .andExpect(status().isServiceUnavailable());
    }
}

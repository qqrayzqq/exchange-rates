package com.github.qqrayzqq.exchangerates.service;

import com.github.qqrayzqq.exchangerates.domain.ExchangeRate;
import com.github.qqrayzqq.exchangerates.dto.ExchangeRateDTO;
import com.github.qqrayzqq.exchangerates.exception.ExternalApiException;
import com.github.qqrayzqq.exchangerates.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {
    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    private ExchangeRateService exchangeRateService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp(){
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();
        exchangeRateService = new ExchangeRateService("http://fake-erste", restClient, exchangeRateRepository);
    }

    @Test
    void getAllExchangeRates_usedbTrue_returnsRatesFromDatabase() {
        ExchangeRate rate = new ExchangeRate();
        rate.setShortName("USD");
        rate.setName("Dollar");
        rate.setCountry("USA");
        rate.setValidFrom(LocalDateTime.of(2022, 4, 26, 0, 0));
        rate.setAmount(1);
        rate.setValBuy(22.0);
        rate.setValSell(23.6);
        rate.setValMid(22.798);

        when(exchangeRateRepository.findAll()).thenReturn(List.of(rate));

        List<ExchangeRateDTO> result = exchangeRateService.getAllExchangeRates(Boolean.TRUE);

        assertThat(result).hasSize(1);
        ExchangeRateDTO dto = result.getFirst();
        assertThat(dto.shortName()).isEqualTo("USD");
        assertThat(dto.name()).isEqualTo("Dollar");
        assertThat(dto.country()).isEqualTo("USA");
        assertThat(dto.validFrom()).isEqualTo(LocalDateTime.of(2022, 4, 26, 0, 0));
        assertThat(dto.amount()).isEqualTo(1);
        assertThat(dto.valBuy()).isEqualTo(22.0);
        assertThat(dto.valSell()).isEqualTo(23.6);
        assertThat(dto.valMid()).isEqualTo(22.798);

        verify(exchangeRateRepository).findAll();
        verify(exchangeRateRepository, never()).saveAll(any());
    }

    @Test
    void getAllExchangeRates_usedbFalse_fetchesFromApiAndSaves() {
        String json = """
            [{"shortName":"USD","validFrom":"2022-04-26T00:00:00","name":"Dollar","country":"USA","move":1.07,"amount":1,"valBuy":22.0,"valSell":23.6,"valMid":22.798,"currBuy":22.228,"currSell":23.368,"currMid":22.798,"version":1,"cnbMid":22.725,"ecbMid":1.075}]
            """;
        mockServer.expect(requestTo("http://fake-erste"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<ExchangeRateDTO> result = exchangeRateService.getAllExchangeRates(Boolean.FALSE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().shortName()).isEqualTo("USD");
        assertThat(result.getFirst().valMid()).isEqualTo(22.798);

        verify(exchangeRateRepository).saveAll(anyList());
    }

    @Test
    void getAllExchangeRates_usedbFalse_whenApiFails_throwsExternalApiException() {
        mockServer.expect(requestTo("http://fake-erste"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> exchangeRateService.getAllExchangeRates(Boolean.FALSE))
                .isInstanceOf(ExternalApiException.class);

        verify(exchangeRateRepository, never()).saveAll(any());
    }
}

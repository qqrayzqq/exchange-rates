package com.github.qqrayzqq.exchangerates.service;

import com.github.qqrayzqq.exchangerates.domain.ExchangeRate;
import com.github.qqrayzqq.exchangerates.dto.ExchangeRateDTO;
import com.github.qqrayzqq.exchangerates.exception.ExternalApiException;
import com.github.qqrayzqq.exchangerates.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.List;

@Service
public class ExchangeRateService {
    private final RestClient restClient;
    private final String ersteApiUrl;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(@Value("${erste.api.url}") String ersteApiUrl, ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.ersteApiUrl = ersteApiUrl;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    public List<ExchangeRateDTO> getAllExchangeRates(Boolean usedb) {
        if(usedb){
            return exchangeRateRepository.findAll().stream().map(ExchangeRate::mapToDTO).toList();
        }else {
            List<ExchangeRateDTO> rates;
            try {
                rates = restClient.get()
                        .uri(ersteApiUrl)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<ExchangeRateDTO>>() {
                        });
            } catch (RestClientException e) {
                throw new ExternalApiException("Failed to fetch exchange rates from the Erste API", e);
            }
            if (rates == null || rates.isEmpty()) {
                throw new ExternalApiException("The Erste API returned no exchange rates", null);
            }
            List<ExchangeRate> result = rates.stream().map(ExchangeRateDTO::toEntity).toList();
            exchangeRateRepository.saveAll(result);
            return rates;
        }
    }
}

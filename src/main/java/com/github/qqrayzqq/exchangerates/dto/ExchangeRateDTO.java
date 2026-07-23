package com.github.qqrayzqq.exchangerates.dto;

import com.github.qqrayzqq.exchangerates.domain.ExchangeRate;

import java.time.LocalDateTime;

public record ExchangeRateDTO(
        String shortName,
        LocalDateTime validFrom,
        String name,
        String country,
        double move,
        int amount,
        double valBuy,
        double valSell,
        double valMid,
        double currBuy,
        double currSell,
        double currMid,
        int version,
        double cnbMid,
        double ecbMid
) {
    public ExchangeRate toEntity(){
        return new ExchangeRate(
                shortName,
                validFrom,
                name,
                country,
                move,
                amount,
                valBuy,
                valSell,
                valMid,
                currBuy,
                currSell,
                currMid,
                version,
                cnbMid,
                ecbMid
        );
    }
}

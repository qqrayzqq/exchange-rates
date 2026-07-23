package com.github.qqrayzqq.exchangerates.domain;

import com.github.qqrayzqq.exchangerates.dto.ExchangeRateDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String shortName;

    private LocalDateTime validFrom;

    private String name;

    private String country;

    private double move;

    private int amount;

    private double valBuy;

    private double valSell;

    private double valMid;

    private double currBuy;

    private double currSell;

    private double currMid;

    private int version;

    private double cnbMid;

    private double ecbMid;

    public ExchangeRate(String shortName, LocalDateTime validFrom, String name, String country, double move,
                        int amount, double valBuy, double valSell, double valMid, double currBuy, double currSell,
                        double currMid, int version, double cnbMid, double ecbMid) {
        this.shortName = shortName;
        this.validFrom = validFrom;
        this.name = name;
        this.country = country;
        this.move = move;
        this.amount = amount;
        this.valBuy = valBuy;
        this.valSell = valSell;
        this.valMid = valMid;
        this.currBuy = currBuy;
        this.currSell = currSell;
        this.currMid = currMid;
        this.version = version;
        this.cnbMid = cnbMid;
        this.ecbMid = ecbMid;
    }

    public ExchangeRateDTO mapToDTO(){
        return new ExchangeRateDTO(
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

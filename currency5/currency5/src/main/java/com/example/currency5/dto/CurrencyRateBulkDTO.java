package com.example.currency5.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CurrencyRateBulkDTO {
    private List<CurrencyRateDTO> currencyRates;

    @Data
    public static class CurrencyRateDTO {
        private String currencyCode;
        private double rate;
        private LocalDateTime lastUpdated;
        private String source;
    }
}
package com.example.currency5.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ConversionHistoryBulkDTO {
    private List<ConversionHistoryDTO> conversionHistories;

    @Data
    public static class ConversionHistoryDTO {
        private String fromCurrency;
        private String toCurrency;
        private double amount;
        private double convertedAmount;
        private LocalDateTime convertedAt;
        private String notes;
        private String status;
        private Long userId;
        private Set<String> currencyRateCodes;
    }
}
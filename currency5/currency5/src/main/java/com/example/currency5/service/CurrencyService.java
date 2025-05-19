package com.example.currency5.service;

import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.model.CurrencyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRateService currencyRateService;

    public CurrencyResponse convert(String from, String to, Double amount) {
        CurrencyRate fromRate = currencyRateService.getCurrencyRateByCode(from)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + from));
        CurrencyRate toRate = currencyRateService.getCurrencyRateByCode(to)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + to));

        double convertedAmount = (amount / fromRate.getRate()) * toRate.getRate();

        CurrencyResponse response = new CurrencyResponse();
        response.setFrom(from);
        response.setTo(to);
        response.setAmount(amount);
        response.setConvertedAmount(convertedAmount);
        return response;
    }
}
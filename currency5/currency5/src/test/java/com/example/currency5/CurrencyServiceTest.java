package com.example.currency5.service;

import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.model.CurrencyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private CurrencyService currencyService;

    private CurrencyRate fromRate;
    private CurrencyRate toRate;

    @BeforeEach
    void setUp() {
        fromRate = new CurrencyRate();
        fromRate.setCurrencyCode("USD");
        fromRate.setRate(1.0);

        toRate = new CurrencyRate();
        toRate.setCurrencyCode("EUR");
        toRate.setRate(0.95);
    }

    @Test
    void convert_ShouldConvertCurrencySuccessfully() {
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.of(fromRate));
        when(currencyRateService.getCurrencyRateByCode("EUR")).thenReturn(Optional.of(toRate));

        CurrencyResponse result = currencyService.convert("USD", "EUR", 100.0);
        assertEquals("USD", result.getFrom());
        assertEquals("EUR", result.getTo());
        assertEquals(100.0, result.getAmount());
        assertEquals(95.0, result.getConvertedAmount());
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
        verify(currencyRateService, times(1)).getCurrencyRateByCode("EUR");
    }

    @Test
    void convert_ShouldThrowExceptionWhenFromCurrencyNotFound() {
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> currencyService.convert("USD", "EUR", 100.0));
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
    }
}
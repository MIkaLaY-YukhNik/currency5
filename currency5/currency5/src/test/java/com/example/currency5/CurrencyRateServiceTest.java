package com.example.currency5.service;

import com.example.currency5.dto.CurrencyRateBulkDTO;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyRateService currencyRateService;

    private CurrencyRate rate1;
    private CurrencyRate rate2;

    @BeforeEach
    void setUp() {
        rate1 = new CurrencyRate();
        rate1.setCurrencyCode("USD");
        rate1.setRate(1.0);
        rate1.setLastUpdated(LocalDateTime.now());
        rate1.setSource("OpenExchangeRates");

        rate2 = new CurrencyRate();
        rate2.setCurrencyCode("EUR");
        rate2.setRate(0.95);
        rate2.setLastUpdated(LocalDateTime.now());
        rate2.setSource("OpenExchangeRates");
    }

    @Test
    void getAllCurrencyRates_ShouldReturnAllRates() {
        when(currencyRateRepository.findAll()).thenReturn(Arrays.asList(rate1, rate2));
        List<CurrencyRate> result = currencyRateService.getAllCurrencyRates();
        assertEquals(2, result.size());
        assertTrue(result.contains(rate1));
        assertTrue(result.contains(rate2));
        verify(currencyRateRepository, times(1)).findAll();
    }

    @Test
    void getCurrencyRateByCode_ShouldReturnRateWhenExists() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(rate1));
        Optional<CurrencyRate> result = currencyRateService.getCurrencyRateByCode("USD");
        assertTrue(result.isPresent());
        assertEquals(rate1, result.get());
        verify(currencyRateRepository, times(1)).findById("USD");
    }

    @Test
    void createCurrencyRate_ShouldSaveAndReturnRate() {
        when(currencyRateRepository.save(rate1)).thenReturn(rate1);
        CurrencyRate result = currencyRateService.createCurrencyRate(rate1);
        assertEquals(rate1, result);
        verify(currencyRateRepository, times(1)).save(rate1);
    }

    @Test
    void createBulkCurrencyRates_ShouldSaveAllRates() {
        CurrencyRateBulkDTO bulkDTO = new CurrencyRateBulkDTO();
        bulkDTO.setCurrencyRates(Arrays.asList(
                new CurrencyRateBulkDTO.CurrencyRateDTO("USD", 1.0, LocalDateTime.now(), "OpenExchangeRates"),
                new CurrencyRateBulkDTO.CurrencyRateDTO("EUR", 0.95, LocalDateTime.now(), "OpenExchangeRates")
        ));
        when(currencyRateRepository.saveAll(anyList())).thenReturn(Arrays.asList(rate1, rate2));
        List<CurrencyRate> result = currencyRateService.createBulkCurrencyRates(bulkDTO);
        assertEquals(2, result.size());
        assertTrue(result.contains(rate1));
        assertTrue(result.contains(rate2));
        verify(currencyRateRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateCurrencyRate_ShouldUpdateAndReturnRate() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(rate1));
        when(currencyRateRepository.save(rate1)).thenReturn(rate1);
        CurrencyRate updatedRate = new CurrencyRate();
        updatedRate.setCurrencyCode("USD");
        updatedRate.setRate(1.05);
        CurrencyRate result = currencyRateService.updateCurrencyRate("USD", updatedRate);
        assertEquals(1.05, result.getRate());
        verify(currencyRateRepository, times(1)).findById("USD");
        verify(currencyRateRepository, times(1)).save(rate1);
    }

    @Test
    void deleteCurrencyRate_ShouldDeleteRate() {
        when(currencyRateRepository.findById("USD")).thenReturn(Optional.of(rate1));
        currencyRateService.deleteCurrencyRate("USD");
        verify(currencyRateRepository, times(1)).findById("USD");
        verify(currencyRateRepository, times(1)).delete(rate1);
    }
}
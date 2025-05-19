package com.example.currency5.service;

import com.example.currency5.dto.ConversionHistoryBulkDTO;
import com.example.currency5.entity.ConversionHistory;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.entity.User;
import com.example.currency5.repository.ConversionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConversionHistoryServiceTest {

    @Mock
    private ConversionHistoryRepository conversionHistoryRepository;

    @Mock
    private UserService userService;

    @Mock
    private CurrencyRateService currencyRateService;

    @InjectMocks
    private ConversionHistoryService conversionHistoryService;

    private ConversionHistory history1;
    private User user;
    private CurrencyRate rate;

    @BeforeEach
    void setUp() {
        user = new User("testuser");
        user.setId(1L);
        rate = new CurrencyRate();
        rate.setCurrencyCode("USD");
        rate.setRate(1.0);
        history1 = new ConversionHistory();
        history1.setId(1L);
        history1.setFromCurrency("USD");
        history1.setToCurrency("EUR");
        history1.setAmount(100.0);
        history1.setConvertedAmount(95.0);
        history1.setConvertedAt(LocalDateTime.now());
        history1.setUser(user);
        history1.setCurrencyRates(new HashSet<>(Arrays.asList(rate)));
    }

    @Test
    void getAllConversionHistories_ShouldReturnAllHistories() {
        when(conversionHistoryRepository.findAll()).thenReturn(Arrays.asList(history1));
        List<ConversionHistory> result = conversionHistoryService.getAllConversionHistories();
        assertEquals(1, result.size());
        assertTrue(result.contains(history1));
        verify(conversionHistoryRepository, times(1)).findAll();
    }

    @Test
    void getConversionHistoryById_ShouldReturnHistoryWhenExists() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(history1));
        Optional<ConversionHistory> result = conversionHistoryService.getConversionHistoryById(1L);
        assertTrue(result.isPresent());
        assertEquals(history1, result.get());
        verify(conversionHistoryRepository, times(1)).findById(1L);
    }

    @Test
    void createConversionHistory_ShouldSaveAndReturnHistory() {
        when(conversionHistoryRepository.save(history1)).thenReturn(history1);
        ConversionHistory result = conversionHistoryService.createConversionHistory(history1);
        assertEquals(history1, result);
        verify(conversionHistoryRepository, times(1)).save(history1);
    }

    @Test
    void createBulkConversionHistories_ShouldSaveAllHistories() {
        ConversionHistoryBulkDTO bulkDTO = new ConversionHistoryBulkDTO();
        ConversionHistoryBulkDTO.ConversionHistoryDTO dto1 = new ConversionHistoryBulkDTO.ConversionHistoryDTO();
        dto1.setFromCurrency("USD");
        dto1.setToCurrency("EUR");
        dto1.setAmount(100.0);
        dto1.setConvertedAmount(95.0);
        dto1.setConvertedAt(LocalDateTime.now());
        dto1.setUserId(1L);
        dto1.setCurrencyRateCodes(new HashSet<>(Arrays.asList("USD")));
        bulkDTO.setConversionHistories(Arrays.asList(dto1));

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(currencyRateService.getCurrencyRateByCode("USD")).thenReturn(Optional.of(rate));
        when(conversionHistoryRepository.saveAll(anyList())).thenReturn(Arrays.asList(history1));

        List<ConversionHistory> result = conversionHistoryService.createBulkConversionHistories(bulkDTO);
        assertEquals(1, result.size());
        assertTrue(result.contains(history1));
        verify(userService, times(1)).getUserById(1L);
        verify(currencyRateService, times(1)).getCurrencyRateByCode("USD");
        verify(conversionHistoryRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateConversionHistory_ShouldUpdateAndReturnHistory() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(history1));
        when(conversionHistoryRepository.save(history1)).thenReturn(history1);
        ConversionHistory updatedHistory = new ConversionHistory();
        updatedHistory.setId(1L);
        updatedHistory.setFromCurrency("EUR");
        updatedHistory.setToCurrency("USD");
        ConversionHistory result = conversionHistoryService.updateConversionHistory(1L, updatedHistory);
        assertEquals("EUR", result.getFromCurrency());
        verify(conversionHistoryRepository, times(1)).findById(1L);
        verify(conversionHistoryRepository, times(1)).save(history1);
    }

    @Test
    void deleteConversionHistory_ShouldDeleteHistory() {
        when(conversionHistoryRepository.findById(1L)).thenReturn(Optional.of(history1));
        conversionHistoryService.deleteConversionHistory(1L);
        verify(conversionHistoryRepository, times(1)).findById(1L);
        verify(conversionHistoryRepository, times(1)).delete(history1);
    }
}
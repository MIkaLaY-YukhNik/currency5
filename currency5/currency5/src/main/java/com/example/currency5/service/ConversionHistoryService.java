package com.example.currency5.service;

import com.example.currency5.dto.ConversionHistoryBulkDTO;
import com.example.currency5.entity.ConversionHistory;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.entity.User;
import com.example.currency5.repository.ConversionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversionHistoryService {

    @Autowired
    private ConversionHistoryRepository conversionHistoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Cacheable(value = "conversionHistoryCache")
    public List<ConversionHistory> getAllConversionHistories() {
        List<ConversionHistory> histories = conversionHistoryRepository.findAll();
        histories.forEach(this::cacheRelatedEntities);
        return histories;
    }

    @Cacheable(value = "conversionHistoryCache", key = "#id")
    public Optional<ConversionHistory> getConversionHistoryById(Long id) {
        Optional<ConversionHistory> history = conversionHistoryRepository.findById(id);
        history.ifPresent(this::cacheRelatedEntities);
        return history;
    }

    @CachePut(value = "conversionHistoryCache", key = "#result.id")
    public ConversionHistory createConversionHistory(ConversionHistory conversionHistory) {
        ConversionHistory savedHistory = conversionHistoryRepository.save(conversionHistory);
        cacheRelatedEntities(savedHistory);
        return savedHistory;
    }

    @Transactional
    @CacheEvict(value = "conversionHistoryCache", allEntries = true)
    public List<ConversionHistory> createBulkConversionHistories(ConversionHistoryBulkDTO bulkDTO) {
        List<ConversionHistory> histories = bulkDTO.getConversionHistories().stream()
                .map(dto -> {
                    ConversionHistory history = new ConversionHistory();
                    history.setFromCurrency(dto.getFromCurrency());
                    history.setToCurrency(dto.getToCurrency());
                    history.setAmount(dto.getAmount());
                    history.setConvertedAmount(dto.getConvertedAmount());
                    history.setConvertedAt(dto.getConvertedAt());
                    history.setNotes(dto.getNotes());
                    history.setStatus(dto.getStatus());

                    User user = userService.getUserById(dto.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
                    history.setUser(user);

                    Set<CurrencyRate> rates = dto.getCurrencyRateCodes().stream()
                            .map(code -> currencyRateService.getCurrencyRateByCode(code)
                                    .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + code)))
                            .collect(Collectors.toSet());
                    history.setCurrencyRates(rates);

                    return history;
                })
                .collect(Collectors.toList());

        List<ConversionHistory> savedHistories = conversionHistoryRepository.saveAll(histories);
        savedHistories.forEach(this::cacheRelatedEntities);
        return savedHistories;
    }

    @Transactional
    @CachePut(value = "conversionHistoryCache", key = "#id")
    public ConversionHistory updateConversionHistory(Long id, ConversionHistory conversionHistoryDetails) {
        ConversionHistory conversionHistory = conversionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConversionHistory not found with id: " + id));
        conversionHistory.setFromCurrency(conversionHistoryDetails.getFromCurrency());
        conversionHistory.setToCurrency(conversionHistoryDetails.getToCurrency());
        conversionHistory.setAmount(conversionHistoryDetails.getAmount());
        conversionHistory.setConvertedAmount(conversionHistoryDetails.getConvertedAmount());
        conversionHistory.setConvertedAt(conversionHistoryDetails.getConvertedAt());
        conversionHistory.setNotes(conversionHistoryDetails.getNotes());
        conversionHistory.setStatus(conversionHistoryDetails.getStatus());
        conversionHistory.setUser(conversionHistoryDetails.getUser());
        conversionHistory.setCurrencyRates(conversionHistoryDetails.getCurrencyRates());
        ConversionHistory updatedHistory = conversionHistoryRepository.save(conversionHistory);
        cacheRelatedEntities(updatedHistory);
        return updatedHistory;
    }

    @Transactional
    @CacheEvict(value = "conversionHistoryCache", key = "#id")
    public void deleteConversionHistory(Long id) {
        ConversionHistory conversionHistory = conversionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ConversionHistory not found with id: " + id));
        evictRelatedEntities(conversionHistory);
        conversionHistoryRepository.delete(conversionHistory);
    }

    private void cacheRelatedEntities(ConversionHistory conversionHistory) {
        if (conversionHistory.getUser() != null) {
            userService.getUserById(conversionHistory.getUser().getId());
        }
        if (conversionHistory.getCurrencyRates() != null) {
            conversionHistory.getCurrencyRates().forEach(rate ->
                    currencyRateService.getCurrencyRateByCode(rate.getCurrencyCode()));
        }
    }

    private void evictRelatedEntities(ConversionHistory conversionHistory) {
        if (conversionHistory.getUser() != null) {
            userService.deleteUser(conversionHistory.getUser().getId());
        }
        if (conversionHistory.getCurrencyRates() != null) {
            conversionHistory.getCurrencyRates().forEach(rate ->
                    currencyRateService.deleteCurrencyRate(rate.getCurrencyCode()));
        }
    }
}
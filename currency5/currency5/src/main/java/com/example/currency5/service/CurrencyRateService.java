package com.example.currency5.service;

import com.example.currency5.dto.CurrencyRateBulkDTO;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CurrencyRateService {

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Cacheable(value = "currencyRateCache")
    public List<CurrencyRate> getAllCurrencyRates() {
        return currencyRateRepository.findAll();
    }

    @Cacheable(value = "currencyRateCache", key = "#currencyCode")
    public Optional<CurrencyRate> getCurrencyRateByCode(String currencyCode) {
        return currencyRateRepository.findById(currencyCode);
    }

    @CachePut(value = "currencyRateCache", key = "#result.currencyCode")
    public CurrencyRate createCurrencyRate(CurrencyRate currencyRate) {
        return currencyRateRepository.save(currencyRate);
    }

    @Transactional
    @CacheEvict(value = "currencyRateCache", allEntries = true)
    public List<CurrencyRate> createBulkCurrencyRates(CurrencyRateBulkDTO bulkDTO) {
        List<CurrencyRate> rates = bulkDTO.getCurrencyRates().stream()
                .map(dto -> {
                    CurrencyRate rate = new CurrencyRate();
                    rate.setCurrencyCode(dto.getCurrencyCode());
                    rate.setRate(dto.getRate());
                    rate.setLastUpdated(dto.getLastUpdated());
                    rate.setSource(dto.getSource());
                    return rate;
                })
                .collect(Collectors.toList());
        return currencyRateRepository.saveAll(rates);
    }

    @Transactional
    @CachePut(value = "currencyRateCache", key = "#currencyCode")
    public CurrencyRate updateCurrencyRate(String currencyCode, CurrencyRate currencyRateDetails) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRate.setRate(currencyRateDetails.getRate());
        currencyRate.setLastUpdated(currencyRateDetails.getLastUpdated());
        currencyRate.setSource(currencyRateDetails.getSource());
        return currencyRateRepository.save(currencyRate);
    }

    @Transactional
    @CacheEvict(value = "currencyRateCache", key = "#currencyCode")
    public void deleteCurrencyRate(String currencyCode) {
        CurrencyRate currencyRate = currencyRateRepository.findById(currencyCode)
                .orElseThrow(() -> new RuntimeException("CurrencyRate not found with code: " + currencyCode));
        currencyRateRepository.delete(currencyRate);
    }
}
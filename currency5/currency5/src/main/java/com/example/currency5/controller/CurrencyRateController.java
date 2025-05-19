package com.example.currency5.controller;

import com.example.currency5.dto.CurrencyRateBulkDTO;
import com.example.currency5.entity.CurrencyRate;
import com.example.currency5.service.CurrencyRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/currency-rates")
public class CurrencyRateController {

    @Autowired
    private CurrencyRateService currencyRateService;

    @GetMapping
    public List<CurrencyRate> getAllCurrencyRates() {
        return currencyRateService.getAllCurrencyRates();
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<CurrencyRate> getCurrencyRateByCode(@PathVariable String currencyCode) {
        return currencyRateService.getCurrencyRateByCode(currencyCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public CurrencyRate createCurrencyRate(@RequestBody CurrencyRate currencyRate) {
        return currencyRateService.createCurrencyRate(currencyRate);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CurrencyRate>> createBulkCurrencyRates(@RequestBody CurrencyRateBulkDTO bulkDTO) {
        List<CurrencyRate> createdRates = currencyRateService.createBulkCurrencyRates(bulkDTO);
        return ResponseEntity.ok(createdRates);
    }

    @PutMapping("/{currencyCode}")
    public ResponseEntity<CurrencyRate> updateCurrencyRate(@PathVariable String currencyCode, @RequestBody CurrencyRate currencyRateDetails) {
        try {
            CurrencyRate updatedRate = currencyRateService.updateCurrencyRate(currencyCode, currencyRateDetails);
            return ResponseEntity.ok(updatedRate);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{currencyCode}")
    public ResponseEntity<Void> deleteCurrencyRate(@PathVariable String currencyCode) {
        try {
            currencyRateService.deleteCurrencyRate(currencyCode);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
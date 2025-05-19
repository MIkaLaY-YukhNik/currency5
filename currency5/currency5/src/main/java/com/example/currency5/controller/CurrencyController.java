package com.example.currency5.controller;

import com.example.currency5.dto.ConvertRequest;
import com.example.currency5.model.CurrencyResponse;
import com.example.currency5.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/convert")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping
    public CurrencyResponse convertCurrency(ConvertRequest request) {
        return currencyService.convert(request.getFrom(), request.getTo(), request.getAmount());
    }
}
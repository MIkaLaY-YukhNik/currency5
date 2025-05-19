package com.example.currency5.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public class ConvertRequest {
    @NotBlank(message = "From currency must not be blank")
    private String from;

    @NotBlank(message = "To currency must not be blank")
    private String to;

    @Positive(message = "Amount must be positive")
    private Double amount;
}
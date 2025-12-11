package com.example.IPS.IPS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    private Long id;
    private LocalDateTime timestamp;
    private String type;
    private double amount;
    private String status;
}

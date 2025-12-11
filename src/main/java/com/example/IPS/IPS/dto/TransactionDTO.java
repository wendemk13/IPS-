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
    private String transactionId;
    private double amount;
    private String type;
    private LocalDateTime timestamp;
    private String status;
    private String reason;

}

package com.example.IPS.IPS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatsDTO {
    private LocalDate date;
    private long totalFailures;
    private long totalSuccesses;
    private double failedAmount;
    private double successAmount;
    private double failurePercentage;
    private double successPercentage;
}

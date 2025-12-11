package com.example.IPS.IPS.dto;


import java.time.LocalDate;

public record DailyTypeSummary(
        LocalDate date,
        long totalFailures,
        long totalSuccesses,
        double failedAmount,
        double successAmount,
        double failurePercentage,
        double successPercentage
) {
}

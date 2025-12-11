package com.example.IPS.IPS.dto;

import java.time.LocalDate;
import java.util.Map;

// DTO for response
public record DailySummary(LocalDate date, long totalFailures, double totalAmount, Map<String, Long> failureByType) {
}

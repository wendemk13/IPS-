package com.example.IPS.IPS.dto;

import java.util.List;

// Overall summary DTO
public record FailuresSummaryResponse(List<DailySummary> dailySummaries, long totalFailures, double totalAmount) {
}
package com.example.IPS.IPS.dto;


public record ServiceFailurePercentage(
        String service,
        long count,
        double percentage
) {
}

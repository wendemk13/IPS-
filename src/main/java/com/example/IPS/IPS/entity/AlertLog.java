package com.example.IPS.IPS.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class AlertLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    // Track if alert has been sent
    private boolean failureAlertSent = false;
    private boolean amountAlertSent = false;

    // Track if threshold is currently exceeded
    private boolean failureThresholdExceeded = false;
    private boolean amountThresholdExceeded = false;
}

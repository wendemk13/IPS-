package com.example.IPS.IPS.controller;

import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/daily-summary/{type}")
    public ResponseEntity<DailyTypeSummary> getDailySummaryByType(
            @PathVariable String type,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getDailySummaryByType(date, type));
    }
}

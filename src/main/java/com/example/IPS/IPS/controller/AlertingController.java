package com.example.IPS.IPS.controller;

import com.example.IPS.IPS.dto.TransactionStatsDTO;
import com.example.IPS.IPS.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/failure")
public class AlertingController {
    private final TransactionService transactionService;

    public AlertingController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //    getStatsByDate
    @PostMapping("/daily/alert")
    public ResponseEntity<TransactionStatsDTO> getDailyAlert(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getStatsByDate(date));
    }


    //send alert for IPS sendIPSAlert
    @PostMapping("/daily/alert/{type}")
    public ResponseEntity<TransactionStatsDTO> getDailyAlertByType(
            @PathVariable String type,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getStatsByDateByType(type, date));
    }

}

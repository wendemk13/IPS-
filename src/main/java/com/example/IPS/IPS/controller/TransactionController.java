package com.example.IPS.IPS.controller;

import com.example.IPS.IPS.dto.DailySummary;
import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.dto.TransactionDTO;
import com.example.IPS.IPS.entity.Transactions;
import com.example.IPS.IPS.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
//    get all failures
    @GetMapping("/failures")
    public ResponseEntity<List<Transactions>> getALlTransactionfailures() {
        return ResponseEntity.ok(transactionService.getAllTransactionsFailures());
    }

    //    get summary by date
    @GetMapping("/daily-summary")
    public ResponseEntity<DailyTypeSummary> getDailySummary(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getDailySummary(date));
    }
    //    get list of failed transactions by date
    @GetMapping("/daily/failed-transactions")
    public ResponseEntity<List<Transactions>> getDailyFailedTransactions(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getDailyFailedTransactions(date));
    }

    //get daily summary by type and date
    @GetMapping("/daily-summary/{type}")
    public ResponseEntity<DailyTypeSummary> getDailySummaryByType(
            @PathVariable String type,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(transactionService.getDailySummaryByType(date, type));
    }

    // transaction save
    @PostMapping("/saveTransaction")
    public ResponseEntity<TransactionDTO> saveTransaction(@RequestBody TransactionDTO dto) {
        TransactionDTO saved = transactionService.saveTransaction(dto);
        return ResponseEntity.ok(saved);
    }


// get single transaaction by id
    @GetMapping("/{id}")
    public ResponseEntity<Transactions> getTransactionById(@PathVariable String id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

//    get transactions by date range
@GetMapping("/range")
public ResponseEntity<List<Transactions>> getAllTransactionsByDateRange(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

    List<Transactions> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
    return ResponseEntity.ok(transactions);
}

    //    get transactions summary by date range
    @GetMapping("/summary/range")
    public ResponseEntity<List<DailySummary>> getDailySummary(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailySummary> summaries = transactionService.getDailySummaryByDateRange(startDate, endDate);
        return ResponseEntity.ok(summaries);
    }










}

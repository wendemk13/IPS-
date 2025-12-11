package com.example.IPS.IPS.controller;

import com.example.IPS.IPS.dto.DailySummary;
import com.example.IPS.IPS.dto.FailuresSummaryResponse;
import com.example.IPS.IPS.entity.TransactionFailure;
import com.example.IPS.IPS.service.TransactionFailureService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FailedTransactionsController {
    public TransactionFailureService transactionFailureService;

    public FailedTransactionsController(TransactionFailureService transactionFailureService) {
        this.transactionFailureService = transactionFailureService;
    }

    //    get all failures
    @GetMapping("/failures")
    public ResponseEntity<List<TransactionFailure>> getFailedTransactions() {
        return new ResponseEntity<>(transactionFailureService.getFailedTransactions(), HttpStatus.OK);
    }

    //    To save failures
    @PostMapping("/failures")
    public void failures(@RequestBody TransactionFailure failures) {
        transactionFailureService.saveTransactionFailure(failures);

    }

    //    daily  failed transaction
    @GetMapping("/failures/daily")
    public ResponseEntity<List<TransactionFailure>> getDailyFailures(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(transactionFailureService.getFailuresByDate(date), HttpStatus.OK);
    }

    //    daily summary for failed transactions
    @GetMapping("/failures/summary/daily")
    public ResponseEntity<DailySummary> getDailyFailuresSummary(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(transactionFailureService.getFailuresSummaryByDate(date), HttpStatus.OK);
    }

    //   get failures by date range
    @GetMapping("/failures/range")
    public ResponseEntity<List<TransactionFailure>> getFailuresByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TransactionFailure> failures = transactionFailureService.getFailuresByDateRange(startDate, endDate);
        return new ResponseEntity<>(failures, HttpStatus.OK);
    }

    //    get failures summary by date range
    @GetMapping("/failures/summary/range")
    public ResponseEntity<List<DailySummary>> getFailuresSummaryByDateRanges(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailySummary> summaries = transactionFailureService.getFailuresSummaryByDateRange(startDate, endDate);
        return new ResponseEntity<>(summaries, HttpStatus.OK);
    }

    @GetMapping("/failures/totalsummary/range")
    public ResponseEntity<FailuresSummaryResponse> getFailuresSummaryByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        FailuresSummaryResponse response = transactionFailureService.getFailuresTotalSummaryByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /// / the daily full summary by type
//@GetMapping("/failures/daily-summary/{type}")
//public ResponseEntity<DailyTypeSummary> dailySummaryByType(
//        @PathVariable String type,
//        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//    return ResponseEntity.ok(transactionFailureService.getDailySummaryByType(date, type));
//}


    //   summary by type
    @GetMapping("/failures/type/{type}")
    public ResponseEntity<DailySummary> type(@PathVariable String type, @RequestParam LocalDate date) {

        return new ResponseEntity<>(transactionFailureService.getByType(type, date), HttpStatus.OK);
    }

//    //    per service percentage
//    @GetMapping("/failures/percentage")
//    public ServiceFailurePercentage percentage(@RequestParam Date start, @RequestParam Date end) {
//        return new ResponseEntity<>(transactionFailureService.getTransactionFailurePercentage(),HttpStatus.OK);
//    }

    //failed amount for all services
    @GetMapping("/failures/amount")
    public List<TransactionFailure> amount(@RequestParam Date start, @RequestParam Date end) {
        return null;
    }

    //specific date
    @GetMapping("/failures/list")
    public List<TransactionFailure> list(@RequestParam Date start, @RequestParam Date end) {
        return null;
    }


}

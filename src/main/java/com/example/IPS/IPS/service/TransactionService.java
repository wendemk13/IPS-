package com.example.IPS.IPS.service;

import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.entity.Transactions;
import com.example.IPS.IPS.repository.TransactionsRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionsRepo transactionsRepo;

    public TransactionService(TransactionsRepo transactionsRepo) {
        this.transactionsRepo = transactionsRepo;
    }

    public DailyTypeSummary getDailySummaryByType(LocalDate date, String type) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // fetch all transactions of that type for the day
        List<Transactions> transactions = transactionsRepo.findAllByTypeAndTimestampBetween(type, startOfDay, endOfDay);

        long totalFailures = transactions.stream()
                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                .count();

        long totalSuccesses = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .count();

        double failedAmount = transactions.stream()
                .filter(t -> "FAILED".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        double successAmount = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();

        long totalTransactions = totalFailures + totalSuccesses;

        double failurePercentage = totalTransactions == 0 ? 0 : (totalFailures * 100.0 / totalTransactions);
        double successPercentage = totalTransactions == 0 ? 0 : (totalSuccesses * 100.0 / totalTransactions);

        return new DailyTypeSummary(
                date,
                totalFailures,
                totalSuccesses,
                failedAmount,
                successAmount,
                failurePercentage,
                successPercentage
        );
    }
}

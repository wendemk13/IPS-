package com.example.IPS.IPS.service;

import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.dto.TransactionDTO;
import com.example.IPS.IPS.dto.TransactionStatsDTO;
import com.example.IPS.IPS.entity.Transactions;
import com.example.IPS.IPS.repository.TransactionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
public class TransactionService {

    private final double failureThresholdPercentage = 10;
    private final double failureThresholdAmount = 1000;

    private final TransactionsRepo transactionsRepo;
    private final AlertingServices alertingService;

    @Autowired
    public TransactionService(
            TransactionsRepo transactionsRepo,
            AlertingServices alertingService) {
        this.transactionsRepo = transactionsRepo;
        this.alertingService = alertingService;

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
                .mapToDouble(t -> t.getAmount())
                .sum();

        double successAmount = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(t -> t.getAmount())
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

    public TransactionStatsDTO getStatsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // fetch all transactions of that type for the day
        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);


        long totalSuccesses = transactions.stream().filter(Transactions::isSuccess).count();
        long totalFailures = transactions.stream().filter(Transactions::isFailure).count();

        double successAmount = transactions.stream()
                .filter(Transactions::isSuccess)
                .mapToDouble(t -> t.getAmount())
                .sum();

        double failedAmount = transactions.stream()
                .filter(Transactions::isFailure)
                .mapToDouble(t -> t.getAmount())
                .sum();

        double failurePercentage = transactions.isEmpty() ? 0 : (totalFailures * 100.0 / transactions.size());
        double successPercentage = transactions.isEmpty() ? 0 : (totalSuccesses * 100.0 / transactions.size());

        if (failurePercentage > failureThresholdPercentage || failedAmount > failureThresholdAmount) {
            alertingService.sendAlert(date, totalFailures, failedAmount, failurePercentage);
        }

        return new TransactionStatsDTO(
                date,
                totalFailures,
                totalSuccesses,
                failedAmount,
                successAmount,
                failurePercentage,
                successPercentage
        );
    }

    public TransactionStatsDTO getStatsByDateByType(String type, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);


        // fetch all transactions of that type for the day
        List<Transactions> transactions = transactionsRepo.findAllByTypeAndTimestampBetween(type, startOfDay, endOfDay);


        long totalSuccesses = transactions.stream().filter(Transactions::isSuccess).count();
        long totalFailures = transactions.stream().filter(Transactions::isFailure).count();

        double successAmount = transactions.stream()
                .filter(Transactions::isSuccess)
                .mapToDouble(t -> t.getAmount())
                .sum();

        double failedAmount = transactions.stream()
                .filter(Transactions::isFailure)
                .mapToDouble(t -> t.getAmount())
                .sum();

        double failurePercentage = transactions.isEmpty() ? 0 : (totalFailures * 100.0 / transactions.size());
        double successPercentage = transactions.isEmpty() ? 0 : (totalSuccesses * 100.0 / transactions.size());

        if (failurePercentage > failureThresholdPercentage || failedAmount > failureThresholdAmount) {
            alertingService.sendAlert(date, totalFailures, failedAmount, failurePercentage);
        }

        return new TransactionStatsDTO(
                date,
                totalFailures,
                totalSuccesses,
                failedAmount,
                successAmount,
                failurePercentage,
                successPercentage
        );
    }

    public Transactions saveTransaction(Transactions transaction) {
        return transactionsRepo.save(transaction);
    }
}

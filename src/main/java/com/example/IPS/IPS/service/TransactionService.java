package com.example.IPS.IPS.service;

import com.example.IPS.IPS.dto.DailySummary;
import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.dto.TransactionDTO;
import com.example.IPS.IPS.dto.TransactionStatsDTO;
import com.example.IPS.IPS.entity.AlertLog;
import com.example.IPS.IPS.entity.Transactions;
import com.example.IPS.IPS.repository.AlertLogRepo;
import com.example.IPS.IPS.repository.TransactionsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class TransactionService {

    private final TransactionsRepo transactionsRepo;
    private final AlertingServices alertingService;
    private final AlertLogRepo alertLogRepo;
    @Value("${app.alert.threshold.percentage}")
    private double failureThresholdPercentage;
    @Value("${app.alert.threshold.amount}")
    private double failureThresholdAmount = 1000;

    @Autowired
    public TransactionService(
            TransactionsRepo transactionsRepo,
            AlertingServices alertingService,
            AlertLogRepo alertLogRepo) {
        this.transactionsRepo = transactionsRepo;
        this.alertingService = alertingService;
        this.alertLogRepo = alertLogRepo;

    }

public List<Transactions> getAllTransactionsFailures() {
        return transactionsRepo.findByStatus("FAILURE");
}


public Transactions getTransactionById(String id) {
    Transactions transaction = transactionsRepo.findByTransactionId(id);

        return transaction;
}
    public List<Transactions> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return transactionsRepo.findByTimestampBetween(start, end);
    }
    public List<DailySummary> getDailySummaryByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(start, end);

        // Group transactions by date
        Map<LocalDate, List<Transactions>> byDate = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getTimestamp().toLocalDate()));

        List<DailySummary> summaries = new ArrayList<>();
        for (LocalDate date : byDate.keySet()) {
            List<Transactions> daily = byDate.get(date);
            long totalFailures = daily.stream().filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus())).count();
            double totalAmount = daily.stream()
                    .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                    .mapToDouble(Transactions::getAmount)
                    .sum();

            Map<String, Long> failureByType = daily.stream()
                    .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                    .collect(Collectors.groupingBy(Transactions::getType, Collectors.counting()));

            summaries.add(new DailySummary(date, totalFailures, totalAmount, failureByType));
        }

        // Sort by date
        summaries.sort(Comparator.comparing(DailySummary::date));
        return summaries;
    }
    public DailyTypeSummary getDailySummary(LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // fetch all transactions of that type for the day
        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);

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


    public List<Transactions> getDailyFailedTransactions(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // fetch all transactions of that type for the day
        return transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);
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


// save transaction
//    public TransactionDTO saveTransaction(TransactionDTO dto) {
//        Transactions transaction = new Transactions();
//        transaction.setTransactionId(dto.getTransactionId());
//        transaction.setAmount(dto.getAmount());
//        transaction.setType(dto.getType());
//        transaction.setTimestamp(dto.getTimestamp());
//        transaction.setStatus(dto.getStatus());
//        transaction.setReason(dto.getReason());
//
//        Transactions saved = transactionsRepo.save(transaction);
//
//        dto.setId(saved.getId());
//        return dto;
//    }


    public TransactionDTO saveTransaction(TransactionDTO dto) {
        // Convert DTO → Entity
        Transactions transaction = new Transactions();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setTimestamp(dto.getTimestamp());
        transaction.setStatus(dto.getStatus());
        transaction.setReason(dto.getReason());

        // Save transaction
        Transactions saved = transactionsRepo.save(transaction);

        //  Immediately check thresholds for this day
        checkThresholdAndSendAlert(saved.getTimestamp().toLocalDate());

        //  Return saved DTO
        dto.setId(saved.getId());
        return dto;
    }

    // check immediately always
//    private void checkThresholdAndSendAlert(LocalDate date) {
//        LocalDateTime startOfDay = date.atStartOfDay();
//        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
//        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);
//
//        long totalFailures = transactions.stream()
//                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
//                .count();
//        long totalSuccesses = transactions.size() - totalFailures;
//
//        double failedAmount = transactions.stream()
//                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
//                .mapToDouble(Transactions::getAmount)
//                .sum();
//
//        double successAmount = transactions.stream()
//                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
//                .mapToDouble(Transactions::getAmount)
//                .sum();
//
//        double failurePercentage = transactions.isEmpty() ? 0
//                : (totalFailures * 100.0 / transactions.size());
//
//        // Trigger alert immediately if thresholds exceeded
//        if (failurePercentage > failureThresholdPercentage || failedAmount > failureThresholdAmount) {
//            alertingService.sendAlert(date, totalFailures, failedAmount, failurePercentage);
//        }
//    }


    private void checkThresholdAndSendAlert(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);

        long totalFailures = transactions.stream()
                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                .count();

        double failedAmount = transactions.stream()
                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(Transactions::getAmount)
                .sum();

        double failurePercentage = transactions.isEmpty() ? 0
                : (totalFailures * 100.0 / transactions.size());

        AlertLog alertLog = alertLogRepo.findByDate(date).orElseGet(() -> {
            AlertLog newLog = new AlertLog();
            newLog.setDate(date);
            return newLog;
        });

        boolean shouldSendAlert = false;
        StringBuilder alertMessage = new StringBuilder();

        // FAILURE PERCENTAGE LOGIC
        if (failurePercentage > failureThresholdPercentage) {
            // Threshold exceeded
            alertLog.setFailureThresholdExceeded(true);

            if (!alertLog.isFailureAlertSent() || lastTransactionIsFailure(transactions)) {
                alertMessage.append("Failure Percentage exceeded: ").append(failurePercentage).append("%\n");
                alertLog.setFailureAlertSent(true);
                shouldSendAlert = true;
            }
        } else {
            // Threshold back to normal
            alertLog.setFailureThresholdExceeded(false);
            alertLog.setFailureAlertSent(false);
        }

        // FAILED AMOUNT LOGIC
        if (failedAmount > failureThresholdAmount) {
            alertLog.setAmountThresholdExceeded(true);

            if (!alertLog.isAmountAlertSent() || lastTransactionIsFailure(transactions)) {
                alertMessage.append("Failed Amount exceeded: $").append(failedAmount).append("\n");
                alertLog.setAmountAlertSent(true);
                shouldSendAlert = true;
            }
        } else {
            alertLog.setAmountThresholdExceeded(false);
            alertLog.setAmountAlertSent(false);
        }

        if (shouldSendAlert) {
            alertingService.sendAlert(date, totalFailures, failedAmount, failurePercentage);
        }

        alertLogRepo.save(alertLog);
    }

    // Helper to check if last transaction is failure
    private boolean lastTransactionIsFailure(List<Transactions> transactions) {
        if (transactions.isEmpty()) return false;
        return "FAILURE".equalsIgnoreCase(transactions.get(transactions.size() - 1).getStatus());
    }


}

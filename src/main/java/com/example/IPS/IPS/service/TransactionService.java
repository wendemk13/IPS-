package com.example.IPS.IPS.service;

import com.example.IPS.IPS.dto.DailyTypeSummary;
import com.example.IPS.IPS.dto.TransactionDTO;
import com.example.IPS.IPS.dto.TransactionStatsDTO;
import com.example.IPS.IPS.entity.Transactions;
import com.example.IPS.IPS.repository.TransactionsRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Transactional
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

    // check
    private void checkThresholdAndSendAlert(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Transactions> transactions = transactionsRepo.findByTimestampBetween(startOfDay, endOfDay);

        long totalFailures = transactions.stream()
                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                .count();
        long totalSuccesses = transactions.size() - totalFailures;

        double failedAmount = transactions.stream()
                .filter(t -> "FAILURE".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(Transactions::getAmount)
                .sum();

        double successAmount = transactions.stream()
                .filter(t -> "SUCCESS".equalsIgnoreCase(t.getStatus()))
                .mapToDouble(Transactions::getAmount)
                .sum();

        double failurePercentage = transactions.isEmpty() ? 0
                : (totalFailures * 100.0 / transactions.size());

        // Trigger alert immediately if thresholds exceeded
        if (failurePercentage > failureThresholdPercentage || failedAmount > failureThresholdAmount) {
            alertingService.sendAlert(date, totalFailures, failedAmount, failurePercentage);
        }
    }

}

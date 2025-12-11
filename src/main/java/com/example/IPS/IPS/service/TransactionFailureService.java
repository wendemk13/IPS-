package com.example.IPS.IPS.service;

import com.example.IPS.IPS.dto.DailySummary;
import com.example.IPS.IPS.dto.FailuresSummaryResponse;
import com.example.IPS.IPS.entity.TransactionFailure;
import com.example.IPS.IPS.repository.TransactionFailureRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionFailureService {
    public final TransactionFailureRepository repository;


    public TransactionFailureService(TransactionFailureRepository repository) {
        this.repository = repository;
    }

    //    get all failures
    public List<TransactionFailure> getFailedTransactions() {

        return repository.findAll();
    }

    //    To save failures
    public TransactionFailure saveTransactionFailure(TransactionFailure transactionFailure) {
        return repository.save(transactionFailure);
    }

    // Fetch all failures for a specific day
    public List<TransactionFailure> getFailuresByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return repository.findByTimestampBetween(startOfDay, endOfDay);
    }

    //    get daily summary
    public DailySummary getFailuresSummaryByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // 23:59:59.999999999

        List<TransactionFailure> failures = repository.findByTimestampBetween(startOfDay, endOfDay);

        long totalFailures = failures.size();
        double totalAmount = failures.stream().mapToDouble(TransactionFailure::getAmount).sum();

        Map<String, Long> failureByType = failures.stream()
                .collect(Collectors.groupingBy(TransactionFailure::getType, Collectors.counting()));

        return new DailySummary(date, totalFailures, totalAmount, failureByType);
    }

    //get total Failure summary By Date Range
    public FailuresSummaryResponse getFailuresTotalSummaryByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<TransactionFailure> failures = repository.findByTimestampBetween(startDateTime, endDateTime);

        // Group by date
        Map<LocalDate, List<TransactionFailure>> grouped = failures.stream()
                .collect(Collectors.groupingBy(f -> f.getTimestamp().toLocalDate()));

        List<DailySummary> dailySummaries = new ArrayList<>();
        long totalFailuresAllDays = 0;
        double totalAmountAllDays = 0;

        for (LocalDate date : grouped.keySet()) {
            List<TransactionFailure> dayFailures = grouped.get(date);
            long totalFailures = dayFailures.size();
            double totalAmount = dayFailures.stream().mapToDouble(TransactionFailure::getAmount).sum();

            Map<String, Long> failureByType = dayFailures.stream()
                    .collect(Collectors.groupingBy(TransactionFailure::getType, Collectors.counting()));

            dailySummaries.add(new DailySummary(date, totalFailures, totalAmount, failureByType));

            // Update overall totals
            totalFailuresAllDays += totalFailures;
            totalAmountAllDays += totalAmount;
        }

        // Sort daily summaries by date
        dailySummaries.sort(Comparator.comparing(DailySummary::date));

        return new FailuresSummaryResponse(dailySummaries, totalFailuresAllDays, totalAmountAllDays);
    }

    //get failures summary by date group
    public List<DailySummary> getFailuresSummaryByDateRange(LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime for the full day range
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<TransactionFailure> failures = repository.findByTimestampBetween(startDateTime, endDateTime);

        // Group by date
        Map<LocalDate, List<TransactionFailure>> grouped = failures.stream()
                .collect(Collectors.groupingBy(f -> f.getTimestamp().toLocalDate()));

        // Convert to DTO
        List<DailySummary> summaries = new ArrayList<>();
        for (LocalDate date : grouped.keySet()) {
            List<TransactionFailure> dayFailures = grouped.get(date);
            long totalFailures = dayFailures.size();
            double totalAmount = dayFailures.stream().mapToDouble(TransactionFailure::getAmount).sum();

            Map<String, Long> failureByType = dayFailures.stream()
                    .collect(Collectors.groupingBy(TransactionFailure::getType, Collectors.counting()));

            summaries.add(new DailySummary(date, totalFailures, totalAmount, failureByType));
        }

        // Optional: sort by date
        summaries.sort(Comparator.comparing(DailySummary::date));

        return summaries;
    }

    //get all failures at range
    public List<TransactionFailure> getFailuresByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        return repository.findAllByTimestampBetween(startDateTime, endDateTime);
    }


    //    get all failed transactions by type
    public DailySummary getByType(String type, LocalDate date) {
        List<TransactionFailure> failures = repository.findAllByType(type);

        if (failures.isEmpty()) {
            return new DailySummary(null, 0, 0.0, Map.of());
        }

        long totalFailures = failures.size();

        double totalAmount = failures.stream()
                .mapToDouble(TransactionFailure::getAmount)
                .sum();

        // count occurrences of type (all have the same type)
        Map<String, Long> failureByType = Map.of(
                type, (long) failures.size()
        );

        // pick latest date
        LocalDate latestDate = failures.stream()
                .map(f -> f.getTimestamp().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(null);

        return new DailySummary(
                latestDate,
                totalFailures,
                totalAmount,
                failureByType
        );
    }


//    public List<DailyTypeSummary> getDailySummaryByType(LocalDate start, LocalDate end, String type) {
//
//        // fetch all failures in the date range for the given type
//        List<TransactionFailure> failures = repository.findAllByTimestampBetween(
//                        start.atStartOfDay(),
//                        end.atTime(LocalTime.MAX)
//                ).stream()
//                .filter(f -> f.getType().equalsIgnoreCase(type))
//                .toList();
//
//        // group by date
//        Map<LocalDate, List<TransactionFailure>> failuresByDate = failures.stream()
//                .collect(Collectors.groupingBy(f -> f.getTimestamp().toLocalDate()));
//
//        return failuresByDate.entrySet().stream()
//                .map(entry -> {
//                    LocalDate date = entry.getKey();
//                    List<TransactionFailure> dailyFailures = entry.getValue();
//
//                    long totalFailures = dailyFailures.size();
//                    double failedAmount = dailyFailures.stream().mapToDouble(TransactionFailure::getAmount).sum();
//
//                    // TODO: Replace with actual total transactions if you have success info
//                    long totalTransactions = totalFailures + 0; // replace 0 with actual success count
//                    double successAmount = 0; // replace with actual success amount
//                    long totalSuccesses = totalTransactions - totalFailures;
//
//                    double failurePercentage = totalTransactions == 0 ? 0 : (totalFailures * 100.0 / totalTransactions);
//                    double successPercentage = totalTransactions == 0 ? 0 : (totalSuccesses * 100.0 / totalTransactions);
//
//                    return new DailyTypeSummary(
//                            date,
//                            totalFailures,
//                            totalSuccesses,
//                            failedAmount,
//                            successAmount,
//                            failurePercentage,
//                            successPercentage
//                    );
//                })
//                .sorted((a, b) -> a.date().compareTo(b.date()))
//                .toList();
//    }


}


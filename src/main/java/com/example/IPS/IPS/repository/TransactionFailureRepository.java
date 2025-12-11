package com.example.IPS.IPS.repository;

import com.example.IPS.IPS.entity.TransactionFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionFailureRepository extends JpaRepository<TransactionFailure, Long> {
    List<TransactionFailure> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<TransactionFailure> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<TransactionFailure> findAllByType(String type);

}
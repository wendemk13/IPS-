package com.example.IPS.IPS.repository;

import com.example.IPS.IPS.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AlertLogRepo extends JpaRepository<AlertLog, Long> {
    //    AlertLog findByDate(LocalDate date);
    Optional<AlertLog> findByDate(LocalDate date);
}

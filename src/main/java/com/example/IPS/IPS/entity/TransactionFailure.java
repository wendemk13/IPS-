package com.example.IPS.IPS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "transaction_failures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionFailure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;
    private String type = String.valueOf(TransactionType.IPS);
    private Double amount;
    private String reason;
    private LocalDateTime timestamp;
}

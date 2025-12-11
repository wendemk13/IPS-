package com.example.IPS.IPS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSACTION_ID", nullable = false, length = 20)
    private String transactionId;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount;

    @Column(name = "TYPE", nullable = false, length = 20)
    private String type;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REASON", length = 50)
    private String reason;

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(this.status);
    }

    public boolean isFailure() {
        return "FAILURE".equalsIgnoreCase(this.status);
    }

}


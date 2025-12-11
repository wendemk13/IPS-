package com.example.IPS.IPS.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AlertingServices {

    private final JavaMailSender mailSender;

    public void sendAlert(LocalDate date, long totalFailures, double failedAmount, double failurePercentage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("wendemk13@gmail.com");
        message.setSubject("Transaction Alert - Failures Exceeded");
        message.setText(
                "Date: " + date +
                        "\nTotal Failures: " + totalFailures +
                        "\nFailed Amount: $" + failedAmount +
                        "\nFailure Percentage: " + failurePercentage + "%"
        );

        mailSender.send(message);
        System.out.println("Alert email sent!");
    }


    //    send alerting for IPS
    public void sendIPSAlert(LocalDate date, long totalFailures, double failedAmount, double failurePercentage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("wendemk13@gmail.com");
        message.setSubject("Transaction Alert - Failures Exceeded");
        message.setText(
                "Date: " + date +
                        "\nTotal Failures: " + totalFailures +
                        "\nFailed Amount: $" + failedAmount +
                        "\nFailure Percentage: " + failurePercentage + "%"
        );

        mailSender.send(message);
        System.out.println("Alert email sent!");
    }

}

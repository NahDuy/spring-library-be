package com.nad.start_spring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity
public class Fine {
    @Id
    private String fineId;

    private double amount;
    private boolean paid;
    private Date paidDate;

    @ManyToOne
    @JoinColumn(name = "loanId")
    private Loan loan;
}

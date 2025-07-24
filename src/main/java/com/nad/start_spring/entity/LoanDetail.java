package com.nad.start_spring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Date;

@Entity
public class LoanDetail {
    @Id
    private String loanDetailId;

    private Date dueDate;
    private Date returnDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "loanId")
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "bookId")
    private Book book;
}
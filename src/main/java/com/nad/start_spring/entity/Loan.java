package com.nad.start_spring.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Loan {
    @Id
    private String loanId;

    private Date borrowDate;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "loan")
    private List<LoanDetail> loanDetails;

    @OneToMany(mappedBy = "loan")
    private List<Fine> fines;
}

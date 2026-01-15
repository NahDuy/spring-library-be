package com.nad.start_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String fineId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_detail_id")
    LoanDetail loanDetail;
    Double amount;
    @Column(length = 255)
    String reason;
    String status;  // PENDING, PAID
    LocalDate createdDate;
    LocalDate paidDate;
    LocalDate dueDate;
}

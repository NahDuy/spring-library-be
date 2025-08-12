package com.nad.start_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class LoanDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String loanDetailId;
    int extendedCount;
    LocalDate dueDate;
    LocalDate returnDate;
    String status;
    int quantity;
    @ManyToOne
    @JoinColumn(name = "loanId")
    Loan loan;

    @ManyToOne
    @JoinColumn(name = "bookId")
    Book book;
}
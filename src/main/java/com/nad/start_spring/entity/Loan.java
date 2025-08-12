package com.nad.start_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String loanId;


    LocalDate startDate;
    LocalDate returnDate;
    @ManyToOne
    @JoinColumn(name = "userId")
    User user;
    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    List<LoanDetail> loanDetails;


}

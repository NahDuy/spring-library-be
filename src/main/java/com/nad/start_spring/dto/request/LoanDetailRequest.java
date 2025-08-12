package com.nad.start_spring.dto.request;

import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Loan;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanDetailRequest {
    @Min(1)
    int quantity;
    LocalDate dueDate;
    LocalDate returnDate;
    int extendedCount;
    String status;
    String loanId;
    String bookId;
}


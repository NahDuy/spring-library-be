package com.nad.start_spring.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanDetailResponse {
    String loanDetailId;
    LocalDate dueDate;
    int extendedCount;
    LocalDate returnDate;
    String status;
    String loanId;
    String bookId;
    int quantity;
}

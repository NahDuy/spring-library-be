package com.nad.start_spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanResponse {
     String loanId;
     String userId;
     UserResponse user;
     String status;
     LocalDate startDate;
     LocalDate returnDate;

     // Thêm danh sách LoanDetail
     List<LoanDetailResponse> loanDetails;
}

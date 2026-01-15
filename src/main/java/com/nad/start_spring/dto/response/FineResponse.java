package com.nad.start_spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineResponse {

    String fineId; // Mã phiếu phạt
    String loanId; // ✅ Mã đơn mượn
    String loanDetailId; // ✅ Mã chi tiết mượn sách liên quan
    Double amount; // Số tiền phạt
    String reason; // Lý do bị phạt (VD: Trả sách trễ)
    LocalDate createdDate; // ✅ Ngày tạo phiếu phạt (đồng bộ với entity Fine)
    LocalDate paidDate; // Ngày đã thanh toán (nếu có)
    LocalDate dueDate; // Hạn nộp phạt
    String status; // Trạng thái: "PENDING", "PAID"
    String bookTitle; // Tiêu đề sách (optional - lấy từ LoanDetail -> Book)
    String userName;
    String fullName;
}

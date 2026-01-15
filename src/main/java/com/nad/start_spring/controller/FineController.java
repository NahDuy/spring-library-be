package com.nad.start_spring.controller;

import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.FineResponse;
import com.nad.start_spring.entity.Fine;
import com.nad.start_spring.mapper.FineMapper;
import com.nad.start_spring.repository.FineRepository;
import com.nad.start_spring.service.FineService;
import com.nad.start_spring.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fine")
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineController {
    FineRepository fineRepository;
    FineMapper fineMapper;
    FineService fineService;
    NotificationService notificationService;

    @GetMapping("/{userId}")
    public ApiResponse<List<FineResponse>> getUserFines(@PathVariable String userId) {
        List<FineResponse> fines = fineRepository.findByLoanDetail_Loan_User_Id(userId)
                .stream()
                .map(fineMapper::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<FineResponse>>builder()
                .status(fines)
                .build();
    }

    @PostMapping("/calculate/{loanDetailId}")
    public ApiResponse<FineResponse> calculateFineAfterReturn(@PathVariable String loanDetailId) {
        FineResponse fine = fineService.calculateFine(loanDetailId);

        if (fine == null) {
            return ApiResponse.<FineResponse>builder()
                    .message("Không có khoản phạt hoặc chưa trả sách trễ")
                    .status(null)
                    .build();
        }

        // Get the Fine entity to send notification
        Fine fineEntity = fineRepository.findByLoanDetail_LoanDetailId(loanDetailId).orElse(null);
        if (fineEntity != null) {
            notificationService.createFineNotification(fineEntity);
        }

        return ApiResponse.<FineResponse>builder()
                .status(fine)
                .message("Đã tính tiền phạt thành công")
                .build();
    }

    @GetMapping("/loanDetail/{loanDetailId}")
    public ApiResponse<FineResponse> getFineByLoanDetail(@PathVariable String loanDetailId) {
        Fine fine = fineRepository.findByLoanDetail_LoanDetailId(loanDetailId)
                .orElse(null);

        if (fine == null) {
            return ApiResponse.<FineResponse>builder()
                    .message("Không có khoản phạt")
                    .status(null)
                    .build();
        }

        return ApiResponse.<FineResponse>builder()
                .status(fineMapper.toResponse(fine))
                .message("Lấy tiền phạt thành công")
                .build();
    }

    @GetMapping
    public ApiResponse<List<FineResponse>> getAllFines(@RequestParam(required = false) String status) {
        return ApiResponse.<List<FineResponse>>builder()
                .status(fineService.getAllFines(status))
                .build();
    }

    @PutMapping("/{fineId}/pay")
    public ApiResponse<FineResponse> payFine(@PathVariable String fineId) {
        return ApiResponse.<FineResponse>builder()
                .status(fineService.payFine(fineId))
                .message("Thanh toán thành công")
                .build();
    }
}

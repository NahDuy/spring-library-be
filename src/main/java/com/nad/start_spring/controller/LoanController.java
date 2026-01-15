package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.LoanRequest;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.mapper.LoanMapper;
import com.nad.start_spring.service.LoanService;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class LoanController {

    LoanService loanService;
    LoanMapper loanMapper;
    UserService userService;

    @PostMapping
    public ApiResponse<LoanResponse> createLoan(@RequestBody LoanRequest request) {
        return ApiResponse.<LoanResponse>builder()
                .status(loanService.createLoan(request))
                .build();
    }

    @PostMapping("/user/{userId}/auto-create")
    public ApiResponse<LoanResponse> autoCreateLoan(@PathVariable String userId) {
        User user = userService.getUserEntityById(userId);
        if (user == null) {
            return ApiResponse.<LoanResponse>builder()
                    .code(404).message("User không tồn tại").build();
        }
        Loan loan = loanService.getOrCreateLoanForUser(user);
        return ApiResponse.<LoanResponse>builder()
                .code(1000)
                .status(loanMapper.toResponse(loan))
                .build();
    }

    @PutMapping("/{loanId}/status")
    public ApiResponse<LoanResponse> updateLoanStatus(
            @PathVariable String loanId,
            @RequestParam String status) {

        Loan updatedLoan = loanService.updateLoanStatus(loanId, status);
        if (updatedLoan == null) {
            return ApiResponse.<LoanResponse>builder()
                    .code(404)
                    .message("Loan không tồn tại")
                    .build();
        }

        return ApiResponse.<LoanResponse>builder()
                .code(1000)
                .status(loanMapper.toResponse(updatedLoan))
                .build();
    }

    @GetMapping("/user/{userId}/active")
    public ApiResponse<List<LoanResponse>> getActiveLoansByUser(@PathVariable String userId) {
        List<String> statuses = List.of("CONFIRMED", "COMPLETED");
        return ApiResponse.<List<LoanResponse>>builder()
                .status(loanService.getLoansByUserAndStatuses(userId, statuses))
                .build();
    }

    @PutMapping("/{loanId}/check-completion")
    public ApiResponse<LoanResponse> checkAndCompleteLoan(@PathVariable String loanId) {
        Loan updatedLoan = loanService.checkAndCompleteLoan(loanId);
        return ApiResponse.<LoanResponse>builder()
                .code(1000)
                .message("Kiểm tra và cập nhật trạng thái thành công")
                .status(loanMapper.toResponse(updatedLoan))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<LoanResponse> getLoanByUserId(@PathVariable String id) {
        return ApiResponse.<LoanResponse>builder()
                .status(loanService.getLoanByUserId(id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteLoan(@PathVariable String id) {
        loanService.deleteLoan(id);
        return ApiResponse.<String>builder()
                .message("Loan deleted successfully")
                .build();
    }

    @GetMapping
    public ApiResponse<List<LoanResponse>> getAllLoans(@RequestParam(required = false) String status) {
        return ApiResponse.<List<LoanResponse>>builder()
                .status(loanService.getAllLoans(status))
                .build();
    }
}

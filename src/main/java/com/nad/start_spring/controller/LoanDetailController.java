package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.LoanDetailRequest;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.dto.response.LoanDetailResponse;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.LoanDetail;
import com.nad.start_spring.mapper.LoanDetailMapper;
import com.nad.start_spring.repository.LoanDetailRepository;
import com.nad.start_spring.repository.LoanRepository;
import com.nad.start_spring.service.BookService;
import com.nad.start_spring.service.FineService;
import com.nad.start_spring.service.LoanDetailService;
import com.nad.start_spring.service.LoanService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/loan-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class LoanDetailController {

    LoanDetailService loanDetailService;
    LoanDetailMapper   loanDetailMapper;
    LoanService loanService;
    LoanDetailRepository loanDetailRepository;
    BookService bookService;
    LoanRepository loanRepository;
    FineService fineService;
    @PostMapping
    public ApiResponse<LoanDetailResponse> createLoanDetail(@RequestBody LoanDetailRequest request) {
        return ApiResponse.<LoanDetailResponse>builder()
                .status(loanDetailService.createLoanDetail(request))
                .build();
    }

    @GetMapping("/loan/{loanId}")
    public ApiResponse<List<LoanDetailResponse>> getLoanDetailsByLoanId(@PathVariable String loanId) {
        return ApiResponse.<List<LoanDetailResponse>>builder()
                .status(loanDetailService.getLoanDetailsByLoanId(loanId))
                .build();
    }
    @GetMapping("/user/{userId}")
    public ApiResponse<List<LoanDetailResponse>> getLoanDetailsByUserId(@PathVariable String userId) {
        return ApiResponse.<List<LoanDetailResponse>>builder()
                .status(loanDetailService.getAllLoanDetailsByUserId(userId))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<LoanDetailResponse> addLoanDetail(@RequestBody LoanDetailRequest request) {
        LoanDetail detail = loanDetailService.addLoanDetail(request);
        return ApiResponse.<LoanDetailResponse>builder()
                .code(1000)
                .status(loanDetailMapper.toResponse(detail))
                .build();
    }
    @PutMapping("/{loanDetailId}/extend")
    public ApiResponse<LoanDetailResponse> extendLoanDetail(
            @PathVariable String loanDetailId,
            @RequestParam int extraDays) {

        LoanDetailResponse updatedLoanDetail = loanDetailService.extendLoanDetail(loanDetailId, extraDays);

        return ApiResponse.<LoanDetailResponse>builder()
                .code(1000)
                .status(updatedLoanDetail)
                .message("Gia hạn mượn sách thành công")
                .build();
    }
    @PutMapping("/{loanDetailId}/return")
    public ApiResponse<LoanDetailResponse> returnBook(@PathVariable String loanDetailId) {
        LoanDetailResponse response = loanDetailService.returnBook(loanDetailId);
        return ApiResponse.<LoanDetailResponse>builder()
                .status(response)
                .build();
    }

}

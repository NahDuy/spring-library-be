
    package com.nad.start_spring.service;

import com.nad.start_spring.dto.response.FineResponse;
import com.nad.start_spring.entity.Fine;
import com.nad.start_spring.entity.LoanDetail;
import com.nad.start_spring.mapper.FineMapper;
import com.nad.start_spring.repository.FineRepository;
import com.nad.start_spring.repository.LoanDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

    @Service
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

    public class FineService {

        LoanDetailRepository loanDetailRepository;
        FineRepository fineRepository;
        FineMapper fineMapper;

        public FineResponse calculateFine(String loanDetailId) {
            LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
                    .orElseThrow(() -> new RuntimeException("LoanDetail không tồn tại"));

            LocalDate dueDate = loanDetail.getDueDate();
            LocalDate returnDate = loanDetail.getReturnDate();

            if (returnDate == null || !returnDate.isAfter(dueDate)) {
                return null;
            }

            if (fineRepository.existsByLoanDetail_LoanDetailId(loanDetailId)) {
                return null;
            }

            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            double fineAmount = daysLate * 5000;

            Fine fine = Fine.builder()
                    .loanDetail(loanDetail)
                    .loan(loanDetail.getLoan())
                    .amount(fineAmount)
                    .reason("Trả sách trễ " + daysLate + " ngày")
                    .status("PENDING")
                    .createdDate(LocalDate.now())
                    .build();

            fineRepository.save(fine);

            return fineMapper.toResponse(fine);
        }

    }



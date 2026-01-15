
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class FineService {

    LoanDetailRepository loanDetailRepository;
    FineRepository fineRepository;
    FineMapper fineMapper;
    ApplicationContext applicationContext;

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
                .user(loanDetail.getLoan().getUser())
                .amount(fineAmount)
                .reason("Trả sách trễ " + daysLate + " ngày")
                .status("PENDING")
                .createdDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        Fine savedFine = fineRepository.save(fine);
        NotificationService notificationService = applicationContext.getBean(NotificationService.class);
        notificationService.createFineNotification(savedFine);

        return fineMapper.toResponse(savedFine);
    }

    public List<FineResponse> getAllFines(String status) {
        List<Fine> fines;
        if (status != null && !status.isEmpty()) {
            fines = fineRepository.findAll().stream()
                    .filter(f -> status.equalsIgnoreCase(f.getStatus()))
                    .collect(Collectors.toList());
        } else {
            fines = fineRepository.findAll();
        }
        return fines.stream().map(fineMapper::toResponse).collect(Collectors.toList());
    }

    public FineResponse payFine(String fineId) {
        Fine fine = fineRepository.findById(fineId).orElseThrow(() -> new RuntimeException("Fine not found"));
        fine.setStatus("PAID");
        fine.setPaidDate(LocalDate.now());
        // Maybe update Loan status if needed? Or Notification?
        return fineMapper.toResponse(fineRepository.save(fine));
    }

    public Fine calculateFineAndReturnEntity(String loanDetailId) {
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
                .user(loanDetail.getLoan().getUser())
                .amount(fineAmount)
                .reason("Trả sách trễ " + daysLate + " ngày")
                .status("PENDING")
                .createdDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        return fineRepository.save(fine);
    }

    public Fine createFineAndSendEmail(String loanDetailId) {
        Fine fine = calculateFineAndReturnEntity(loanDetailId);
        if (fine != null) {
            NotificationService notificationService = applicationContext.getBean(NotificationService.class);
            notificationService.createFineNotification(fine);
        }
        return fine;
    }
}

package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.LoanRequest;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.LoanDetail;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.mapper.LoanMapper;
import com.nad.start_spring.repository.FineRepository;
import com.nad.start_spring.repository.LoanRepository;
import com.nad.start_spring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanService {

    LoanRepository loanRepository;
    UserRepository userRepository;
    LoanMapper loanMapper;
    FineService fineService;
    FineRepository fineRepository;
    public LoanResponse createLoan(LoanRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Loan loan = loanMapper.toLoan(request);
        loan.setUser(user);
        loan.setStartDate(LocalDate.now());
        loanRepository.save(loan);
        return loanMapper.toResponse(loan);
    }
    public Loan getLoanEntityById(String loanId) {
        Optional<Loan> loan = loanRepository.findById(loanId);
        return loan.orElse(null);
    }
    @Transactional
    public Loan updateLoanStatus(String loanId, String newStatus) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isEmpty()) {
            return null;
        }
        Loan loan = loanOpt.get();
        loan.setStatus(newStatus);
        if ("CONFIRMED".equalsIgnoreCase(newStatus)) {
            loan.setStartDate(LocalDate.now());
            for (LoanDetail detail : loan.getLoanDetails()) {
                if (detail.getDueDate() == null) {
                    detail.setDueDate(LocalDate.now().plusDays(7));
                }
                detail.setStatus("BORROWED");
            }
        }
        if ("COMPLETED".equalsIgnoreCase(newStatus)) {
            loan.setReturnDate(LocalDate.now());

            for (LoanDetail detail : loan.getLoanDetails()) {
                detail.setReturnDate(LocalDate.now());
                detail.setStatus("RETURNED");

                boolean existsFine = fineRepository.existsByLoanDetail_LoanDetailId(detail.getLoanDetailId());
                if (!existsFine) {
                    fineService.calculateFine(detail.getLoanDetailId());
                }
            }
        }


        return loanRepository.save(loan);
    }
        @Transactional
    public Loan checkAndCompleteLoan(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan không tồn tại"));

        boolean allReturned = loan.getLoanDetails().stream()
                .allMatch(detail -> "RETURNED".equalsIgnoreCase(detail.getStatus()));

        if (allReturned) {
            loan.setReturnDate(LocalDate.now());
            loan.setStatus("COMPLETED");
            loanRepository.save(loan);
        }

        return loan;
    }

    public List<LoanResponse> getLoansByUserAndStatuses(String userId, List<String> statuses) {
        List<Loan> loans = loanRepository.findByUser_IdAndStatusIn(userId, statuses);
        return loans.stream()
                .map(loanMapper::toResponse)
                .collect(Collectors.toList());
    }


    public LoanResponse getLoanByUserId(String userId) {
        Loan loan = loanRepository.findByUser_IdAndStatus(userId, "PENDING")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Loan đang mở cho user"));
        return loanMapper.toResponse(loan);
    }


    public Loan getOrCreateLoanForUser(User user) {
        Optional<Loan> pendingLoan = loanRepository.findByUserAndStatus(user, "PENDING");
        if (pendingLoan.isPresent()) {
            return pendingLoan.get();
        }
        Loan newLoan = new Loan();
        newLoan.setUser(user);
        newLoan.setStatus("PENDING");
        return loanRepository.save(newLoan);
    }
    public void deleteLoan(String id) {
        loanRepository.deleteById(id);
    }
}

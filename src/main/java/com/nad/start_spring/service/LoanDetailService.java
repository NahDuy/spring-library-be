package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.LoanDetailRequest;
import com.nad.start_spring.dto.response.LoanDetailResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.LoanDetail;
import com.nad.start_spring.enums.LoanDetailStatus;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.mapper.LoanDetailMapper;
import com.nad.start_spring.repository.BookRepository;
import com.nad.start_spring.repository.LoanDetailRepository;
import com.nad.start_spring.repository.LoanRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanDetailService {

    LoanDetailRepository loanDetailRepository;
    LoanRepository loanRepository;
    BookRepository bookRepository;
    LoanDetailMapper mapper;
    FineService fineService;
    LoanService loanService;
    BookService bookService;

    public LoanDetailResponse createLoanDetail(LoanDetailRequest request) {
        if (!loanRepository.existsById(request.getLoanId())) {
            throw new AppException(ErrorCode.LOAN_NOT_FOUND);
        }
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (book.getAvailableCopies() < request.getQuantity()) {
            throw new AppException(ErrorCode.NOT_ENOUGH_COPY_BOOKS);
        }
        book.setAvailableCopies(book.getAvailableCopies() - request.getQuantity());
        bookRepository.save(book);
        LoanDetail loanDetail = mapper.toLoanDetail(request);
        loanDetail.setStatus(String.valueOf(LoanDetailStatus.BORROWED));
        return mapper.toResponse(loanDetailRepository.save(loanDetail));
    }

    public List<LoanDetailResponse> getLoanDetailsByLoanId(String loanId) {
        return loanDetailRepository.findByLoan_LoanId(loanId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    public LoanDetailResponse extendLoanDetail(String loanDetailId, int extraDays) throws RuntimeException {
        LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy LoanDetail"));

        if (loanDetail.getDueDate() == null) {
            throw new RuntimeException("LoanDetail chưa có hạn trả");
        }

        if (loanDetail.getExtendedCount() >= 2) {
            throw new RuntimeException("Đã đạt giới hạn gia hạn");
        }

        if (loanDetail.getDueDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Không thể gia hạn khi đã quá hạn");
        }

        loanDetail.setDueDate(loanDetail.getDueDate().plusDays(extraDays));

        loanDetail.setExtendedCount(loanDetail.getExtendedCount() + 1);

        loanDetailRepository.save(loanDetail);

        return mapper.toResponse(loanDetail);
    }


    public LoanDetailResponse returnBook(String loanDetailId) {
        LoanDetail loanDetail = loanDetailRepository.findById(loanDetailId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if (loanDetail.getStatus().equals(String.valueOf(LoanDetailStatus.RETURNED))) {
            throw new AppException(ErrorCode.BOOK_ALREADY_RETURNED);
        }

        Book book = loanDetail.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + loanDetail.getQuantity());
        bookRepository.save(book);
        loanDetail.setReturnDate(LocalDate.now());
        loanDetail.setStatus(String.valueOf(LoanDetailStatus.RETURNED));
        loanDetailRepository.save(loanDetail);

        fineService.calculateFine(loanDetailId);

        return mapper.toResponse(loanDetail);
    }

    public List<LoanDetailResponse> getAllLoanDetailsByUserId(String userId) {
        return loanDetailRepository.findByLoan_User_IdAndStatus(userId, "RETURNED")
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
    public LoanDetail addLoanDetail(LoanDetailRequest request) {
        Loan loan = loanService.getLoanEntityById(request.getLoanId());
        Book book = bookService.getBookById(request.getBookId());

        if (loan == null || book == null) {
            throw new RuntimeException("Loan hoặc Book không tồn tại");
        }

        Optional<LoanDetail> existing = loanDetailRepository.findByLoanAndBook(loan, book);
        LoanDetail detail;
        if (existing.isPresent()) {
            detail = existing.get();
            detail.setQuantity(detail.getQuantity() + request.getQuantity());
        } else {
            detail = new LoanDetail();
            detail.setLoan(loan);
            detail.setBook(book);
            detail.setQuantity(request.getQuantity());
            detail.setDueDate(null);
        }

        return loanDetailRepository.save(detail);
    }


}

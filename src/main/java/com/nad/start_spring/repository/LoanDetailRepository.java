package com.nad.start_spring.repository;

import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanDetailRepository extends JpaRepository<LoanDetail, String> {
    List<LoanDetail> findByLoan_LoanId(String loanId);
    Optional<LoanDetail> findByLoanAndBook(Loan loan, Book book);
    List<LoanDetail> findByLoan(Loan loan);
    List<LoanDetail> findByLoan_User_Id(String userId);
    List<LoanDetail> findByLoan_User_IdAndStatus(String userId, String status);
}